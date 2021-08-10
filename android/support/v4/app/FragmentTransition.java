package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* access modifiers changed from: package-private */
public class FragmentTransition {
    private static final int[] INVERSE_OPS = {0, 3, 0, 1, 5, 4, 7, 6, 9, 8};
    private static final FragmentTransitionImpl PLATFORM_IMPL = (Build.VERSION.SDK_INT >= 21 ? new FragmentTransitionCompat21() : null);
    private static final FragmentTransitionImpl SUPPORT_IMPL = resolveSupportImpl();

    FragmentTransition() {
    }

    private static FragmentTransitionImpl resolveSupportImpl() {
        try {
            return (FragmentTransitionImpl) Class.forName("android.support.transition.FragmentTransitionSupport").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            return null;
        }
    }

    static void startTransitions(FragmentManagerImpl fragmentManager, ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex, boolean isReordered) {
        if (fragmentManager.mCurState >= 1) {
            SparseArray<FragmentContainerTransition> transitioningFragments = new SparseArray<>();
            for (int i = startIndex; i < endIndex; i++) {
                BackStackRecord record = records.get(i);
                if (isRecordPop.get(i).booleanValue()) {
                    calculatePopFragments(record, transitioningFragments, isReordered);
                } else {
                    calculateFragments(record, transitioningFragments, isReordered);
                }
            }
            if (transitioningFragments.size() != 0) {
                View nonExistentView = new View(fragmentManager.mHost.getContext());
                int numContainers = transitioningFragments.size();
                for (int i2 = 0; i2 < numContainers; i2++) {
                    int containerId = transitioningFragments.keyAt(i2);
                    ArrayMap<String, String> nameOverrides = calculateNameOverrides(containerId, records, isRecordPop, startIndex, endIndex);
                    FragmentContainerTransition containerTransition = transitioningFragments.valueAt(i2);
                    if (isReordered) {
                        configureTransitionsReordered(fragmentManager, containerId, containerTransition, nonExistentView, nameOverrides);
                    } else {
                        configureTransitionsOrdered(fragmentManager, containerId, containerTransition, nonExistentView, nameOverrides);
                    }
                }
            }
        }
    }

    private static ArrayMap<String, String> calculateNameOverrides(int containerId, ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
        ArrayList<String> sources;
        ArrayList<String> targets;
        ArrayMap<String, String> nameOverrides = new ArrayMap<>();
        for (int recordNum = endIndex - 1; recordNum >= startIndex; recordNum--) {
            BackStackRecord record = records.get(recordNum);
            if (record.interactsWith(containerId)) {
                boolean isPop = isRecordPop.get(recordNum).booleanValue();
                if (record.mSharedElementSourceNames != null) {
                    int numSharedElements = record.mSharedElementSourceNames.size();
                    if (isPop) {
                        targets = record.mSharedElementSourceNames;
                        sources = record.mSharedElementTargetNames;
                    } else {
                        sources = record.mSharedElementSourceNames;
                        targets = record.mSharedElementTargetNames;
                    }
                    for (int i = 0; i < numSharedElements; i++) {
                        String sourceName = sources.get(i);
                        String targetName = targets.get(i);
                        String previousTarget = nameOverrides.remove(targetName);
                        if (previousTarget != null) {
                            nameOverrides.put(sourceName, previousTarget);
                        } else {
                            nameOverrides.put(sourceName, targetName);
                        }
                    }
                }
            }
        }
        return nameOverrides;
    }

    private static void configureTransitionsReordered(FragmentManagerImpl fragmentManager, int containerId, FragmentContainerTransition fragments, View nonExistentView, ArrayMap<String, String> nameOverrides) {
        ViewGroup sceneRoot;
        Fragment inFragment;
        Fragment outFragment;
        FragmentTransitionImpl impl;
        Object exitTransition;
        if (fragmentManager.mContainer.onHasView()) {
            sceneRoot = (ViewGroup) fragmentManager.mContainer.onFindViewById(containerId);
        } else {
            sceneRoot = null;
        }
        if (sceneRoot != null && (impl = chooseImpl((outFragment = fragments.firstOut), (inFragment = fragments.lastIn))) != null) {
            boolean inIsPop = fragments.lastInIsPop;
            boolean outIsPop = fragments.firstOutIsPop;
            ArrayList<View> sharedElementsIn = new ArrayList<>();
            ArrayList<View> sharedElementsOut = new ArrayList<>();
            Object enterTransition = getEnterTransition(impl, inFragment, inIsPop);
            Object exitTransition2 = getExitTransition(impl, outFragment, outIsPop);
            Object sharedElementTransition = configureSharedElementsReordered(impl, sceneRoot, nonExistentView, nameOverrides, fragments, sharedElementsOut, sharedElementsIn, enterTransition, exitTransition2);
            if (enterTransition == null && sharedElementTransition == null) {
                exitTransition = exitTransition2;
                if (exitTransition == null) {
                    return;
                }
            } else {
                exitTransition = exitTransition2;
            }
            ArrayList<View> exitingViews = configureEnteringExitingViews(impl, exitTransition, outFragment, sharedElementsOut, nonExistentView);
            ArrayList<View> enteringViews = configureEnteringExitingViews(impl, enterTransition, inFragment, sharedElementsIn, nonExistentView);
            setViewVisibility(enteringViews, 4);
            Object transition = mergeTransitions(impl, enterTransition, exitTransition, sharedElementTransition, inFragment, inIsPop);
            if (transition != null) {
                replaceHide(impl, exitTransition, outFragment, exitingViews);
                ArrayList<String> inNames = impl.prepareSetNameOverridesReordered(sharedElementsIn);
                impl.scheduleRemoveTargets(transition, enterTransition, enteringViews, exitTransition, exitingViews, sharedElementTransition, sharedElementsIn);
                impl.beginDelayedTransition(sceneRoot, transition);
                impl.setNameOverridesReordered(sceneRoot, sharedElementsOut, sharedElementsIn, inNames, nameOverrides);
                setViewVisibility(enteringViews, 0);
                impl.swapSharedElementTargets(sharedElementTransition, sharedElementsOut, sharedElementsIn);
            }
        }
    }

    private static void replaceHide(FragmentTransitionImpl impl, Object exitTransition, Fragment exitingFragment, final ArrayList<View> exitingViews) {
        if (exitingFragment != null && exitTransition != null && exitingFragment.mAdded && exitingFragment.mHidden && exitingFragment.mHiddenChanged) {
            exitingFragment.setHideReplaced(true);
            impl.scheduleHideFragmentView(exitTransition, exitingFragment.getView(), exitingViews);
            OneShotPreDrawListener.add(exitingFragment.mContainer, new Runnable() {
                /* class android.support.v4.app.FragmentTransition.AnonymousClass1 */

                public void run() {
                    FragmentTransition.setViewVisibility(exitingViews, 4);
                }
            });
        }
    }

    private static void configureTransitionsOrdered(FragmentManagerImpl fragmentManager, int containerId, FragmentContainerTransition fragments, View nonExistentView, ArrayMap<String, String> nameOverrides) {
        ViewGroup sceneRoot;
        Fragment inFragment;
        Fragment outFragment;
        FragmentTransitionImpl impl;
        Object exitTransition;
        Object exitTransition2;
        if (fragmentManager.mContainer.onHasView()) {
            sceneRoot = (ViewGroup) fragmentManager.mContainer.onFindViewById(containerId);
        } else {
            sceneRoot = null;
        }
        if (sceneRoot != null && (impl = chooseImpl((outFragment = fragments.firstOut), (inFragment = fragments.lastIn))) != null) {
            boolean inIsPop = fragments.lastInIsPop;
            boolean outIsPop = fragments.firstOutIsPop;
            Object enterTransition = getEnterTransition(impl, inFragment, inIsPop);
            Object exitTransition3 = getExitTransition(impl, outFragment, outIsPop);
            ArrayList<View> sharedElementsOut = new ArrayList<>();
            ArrayList<View> sharedElementsIn = new ArrayList<>();
            Object sharedElementTransition = configureSharedElementsOrdered(impl, sceneRoot, nonExistentView, nameOverrides, fragments, sharedElementsOut, sharedElementsIn, enterTransition, exitTransition3);
            if (enterTransition == null && sharedElementTransition == null) {
                exitTransition = exitTransition3;
                if (exitTransition == null) {
                    return;
                }
            } else {
                exitTransition = exitTransition3;
            }
            ArrayList<View> exitingViews = configureEnteringExitingViews(impl, exitTransition, outFragment, sharedElementsOut, nonExistentView);
            if (exitingViews == null || exitingViews.isEmpty()) {
                exitTransition2 = null;
            } else {
                exitTransition2 = exitTransition;
            }
            impl.addTarget(enterTransition, nonExistentView);
            Object transition = mergeTransitions(impl, enterTransition, exitTransition2, sharedElementTransition, inFragment, fragments.lastInIsPop);
            if (transition != null) {
                ArrayList<View> enteringViews = new ArrayList<>();
                impl.scheduleRemoveTargets(transition, enterTransition, enteringViews, exitTransition2, exitingViews, sharedElementTransition, sharedElementsIn);
                scheduleTargetChange(impl, sceneRoot, inFragment, nonExistentView, sharedElementsIn, enterTransition, enteringViews, exitTransition2, exitingViews);
                impl.setNameOverridesOrdered(sceneRoot, sharedElementsIn, nameOverrides);
                impl.beginDelayedTransition(sceneRoot, transition);
                impl.scheduleNameReset(sceneRoot, sharedElementsIn, nameOverrides);
            }
        }
    }

    private static void scheduleTargetChange(final FragmentTransitionImpl impl, ViewGroup sceneRoot, final Fragment inFragment, final View nonExistentView, final ArrayList<View> sharedElementsIn, final Object enterTransition, final ArrayList<View> enteringViews, final Object exitTransition, final ArrayList<View> exitingViews) {
        OneShotPreDrawListener.add(sceneRoot, new Runnable() {
            /* class android.support.v4.app.FragmentTransition.AnonymousClass2 */

            public void run() {
                Object obj = enterTransition;
                if (obj != null) {
                    impl.removeTarget(obj, nonExistentView);
                    enteringViews.addAll(FragmentTransition.configureEnteringExitingViews(impl, enterTransition, inFragment, sharedElementsIn, nonExistentView));
                }
                if (exitingViews != null) {
                    if (exitTransition != null) {
                        ArrayList<View> tempExiting = new ArrayList<>();
                        tempExiting.add(nonExistentView);
                        impl.replaceTargets(exitTransition, exitingViews, tempExiting);
                    }
                    exitingViews.clear();
                    exitingViews.add(nonExistentView);
                }
            }
        });
    }

    private static FragmentTransitionImpl chooseImpl(Fragment outFragment, Fragment inFragment) {
        ArrayList<Object> transitions = new ArrayList<>();
        if (outFragment != null) {
            Object exitTransition = outFragment.getExitTransition();
            if (exitTransition != null) {
                transitions.add(exitTransition);
            }
            Object returnTransition = outFragment.getReturnTransition();
            if (returnTransition != null) {
                transitions.add(returnTransition);
            }
            Object sharedReturnTransition = outFragment.getSharedElementReturnTransition();
            if (sharedReturnTransition != null) {
                transitions.add(sharedReturnTransition);
            }
        }
        if (inFragment != null) {
            Object enterTransition = inFragment.getEnterTransition();
            if (enterTransition != null) {
                transitions.add(enterTransition);
            }
            Object reenterTransition = inFragment.getReenterTransition();
            if (reenterTransition != null) {
                transitions.add(reenterTransition);
            }
            Object sharedEnterTransition = inFragment.getSharedElementEnterTransition();
            if (sharedEnterTransition != null) {
                transitions.add(sharedEnterTransition);
            }
        }
        if (transitions.isEmpty()) {
            return null;
        }
        FragmentTransitionImpl fragmentTransitionImpl = PLATFORM_IMPL;
        if (fragmentTransitionImpl != null && canHandleAll(fragmentTransitionImpl, transitions)) {
            return PLATFORM_IMPL;
        }
        FragmentTransitionImpl fragmentTransitionImpl2 = SUPPORT_IMPL;
        if (fragmentTransitionImpl2 != null && canHandleAll(fragmentTransitionImpl2, transitions)) {
            return SUPPORT_IMPL;
        }
        if (PLATFORM_IMPL == null && SUPPORT_IMPL == null) {
            return null;
        }
        throw new IllegalArgumentException("Invalid Transition types");
    }

    private static boolean canHandleAll(FragmentTransitionImpl impl, List<Object> transitions) {
        int size = transitions.size();
        for (int i = 0; i < size; i++) {
            if (!impl.canHandle(transitions.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static Object getSharedElementTransition(FragmentTransitionImpl impl, Fragment inFragment, Fragment outFragment, boolean isPop) {
        Object obj;
        if (inFragment == null || outFragment == null) {
            return null;
        }
        if (isPop) {
            obj = outFragment.getSharedElementReturnTransition();
        } else {
            obj = inFragment.getSharedElementEnterTransition();
        }
        return impl.wrapTransitionInSet(impl.cloneTransition(obj));
    }

    private static Object getEnterTransition(FragmentTransitionImpl impl, Fragment inFragment, boolean isPop) {
        Object obj;
        if (inFragment == null) {
            return null;
        }
        if (isPop) {
            obj = inFragment.getReenterTransition();
        } else {
            obj = inFragment.getEnterTransition();
        }
        return impl.cloneTransition(obj);
    }

    private static Object getExitTransition(FragmentTransitionImpl impl, Fragment outFragment, boolean isPop) {
        Object obj;
        if (outFragment == null) {
            return null;
        }
        if (isPop) {
            obj = outFragment.getReturnTransition();
        } else {
            obj = outFragment.getExitTransition();
        }
        return impl.cloneTransition(obj);
    }

    private static Object configureSharedElementsReordered(final FragmentTransitionImpl impl, ViewGroup sceneRoot, View nonExistentView, ArrayMap<String, String> nameOverrides, FragmentContainerTransition fragments, ArrayList<View> sharedElementsOut, ArrayList<View> sharedElementsIn, Object enterTransition, Object exitTransition) {
        Object sharedElementTransition;
        Object sharedElementTransition2;
        Object sharedElementTransition3;
        final View epicenterView;
        final Rect epicenter;
        final ArrayMap<String, View> inSharedElements;
        final Fragment inFragment = fragments.lastIn;
        final Fragment outFragment = fragments.firstOut;
        if (inFragment != null) {
            inFragment.getView().setVisibility(0);
        }
        if (inFragment != null) {
            if (outFragment != null) {
                final boolean inIsPop = fragments.lastInIsPop;
                if (nameOverrides.isEmpty()) {
                    sharedElementTransition = null;
                } else {
                    sharedElementTransition = getSharedElementTransition(impl, inFragment, outFragment, inIsPop);
                }
                ArrayMap<String, View> outSharedElements = captureOutSharedElements(impl, nameOverrides, sharedElementTransition, fragments);
                ArrayMap<String, View> inSharedElements2 = captureInSharedElements(impl, nameOverrides, sharedElementTransition, fragments);
                if (nameOverrides.isEmpty()) {
                    if (outSharedElements != null) {
                        outSharedElements.clear();
                    }
                    if (inSharedElements2 != null) {
                        inSharedElements2.clear();
                    }
                    sharedElementTransition2 = null;
                } else {
                    addSharedElementsWithMatchingNames(sharedElementsOut, outSharedElements, nameOverrides.keySet());
                    addSharedElementsWithMatchingNames(sharedElementsIn, inSharedElements2, nameOverrides.values());
                    sharedElementTransition2 = sharedElementTransition;
                }
                if (enterTransition == null && exitTransition == null && sharedElementTransition2 == null) {
                    return null;
                }
                callSharedElementStartEnd(inFragment, outFragment, inIsPop, outSharedElements, true);
                if (sharedElementTransition2 != null) {
                    sharedElementsIn.add(nonExistentView);
                    impl.setSharedElementTargets(sharedElementTransition2, nonExistentView, sharedElementsOut);
                    sharedElementTransition3 = sharedElementTransition2;
                    inSharedElements = inSharedElements2;
                    setOutEpicenter(impl, sharedElementTransition2, exitTransition, outSharedElements, fragments.firstOutIsPop, fragments.firstOutTransaction);
                    Rect epicenter2 = new Rect();
                    View epicenterView2 = getInEpicenterView(inSharedElements, fragments, enterTransition, inIsPop);
                    if (epicenterView2 != null) {
                        impl.setEpicenter(enterTransition, epicenter2);
                    }
                    epicenter = epicenter2;
                    epicenterView = epicenterView2;
                } else {
                    sharedElementTransition3 = sharedElementTransition2;
                    inSharedElements = inSharedElements2;
                    epicenter = null;
                    epicenterView = null;
                }
                OneShotPreDrawListener.add(sceneRoot, new Runnable() {
                    /* class android.support.v4.app.FragmentTransition.AnonymousClass3 */

                    public void run() {
                        FragmentTransition.callSharedElementStartEnd(inFragment, outFragment, inIsPop, inSharedElements, false);
                        View view = epicenterView;
                        if (view != null) {
                            impl.getBoundsOnScreen(view, epicenter);
                        }
                    }
                });
                return sharedElementTransition3;
            }
        }
        return null;
    }

    private static void addSharedElementsWithMatchingNames(ArrayList<View> views, ArrayMap<String, View> sharedElements, Collection<String> nameOverridesSet) {
        for (int i = sharedElements.size() - 1; i >= 0; i--) {
            View view = sharedElements.valueAt(i);
            if (nameOverridesSet.contains(ViewCompat.getTransitionName(view))) {
                views.add(view);
            }
        }
    }

    private static Object configureSharedElementsOrdered(final FragmentTransitionImpl impl, ViewGroup sceneRoot, final View nonExistentView, final ArrayMap<String, String> nameOverrides, final FragmentContainerTransition fragments, final ArrayList<View> sharedElementsOut, final ArrayList<View> sharedElementsIn, final Object enterTransition, Object exitTransition) {
        Object sharedElementTransition;
        final Object sharedElementTransition2;
        final Rect inEpicenter;
        final Fragment inFragment = fragments.lastIn;
        final Fragment outFragment = fragments.firstOut;
        if (inFragment != null) {
            if (outFragment != null) {
                final boolean inIsPop = fragments.lastInIsPop;
                if (nameOverrides.isEmpty()) {
                    sharedElementTransition = null;
                } else {
                    sharedElementTransition = getSharedElementTransition(impl, inFragment, outFragment, inIsPop);
                }
                ArrayMap<String, View> outSharedElements = captureOutSharedElements(impl, nameOverrides, sharedElementTransition, fragments);
                if (nameOverrides.isEmpty()) {
                    sharedElementTransition2 = null;
                } else {
                    sharedElementsOut.addAll(outSharedElements.values());
                    sharedElementTransition2 = sharedElementTransition;
                }
                if (enterTransition == null && exitTransition == null && sharedElementTransition2 == null) {
                    return null;
                }
                callSharedElementStartEnd(inFragment, outFragment, inIsPop, outSharedElements, true);
                if (sharedElementTransition2 != null) {
                    Rect inEpicenter2 = new Rect();
                    impl.setSharedElementTargets(sharedElementTransition2, nonExistentView, sharedElementsOut);
                    setOutEpicenter(impl, sharedElementTransition2, exitTransition, outSharedElements, fragments.firstOutIsPop, fragments.firstOutTransaction);
                    if (enterTransition != null) {
                        impl.setEpicenter(enterTransition, inEpicenter2);
                    }
                    inEpicenter = inEpicenter2;
                } else {
                    inEpicenter = null;
                }
                OneShotPreDrawListener.add(sceneRoot, new Runnable() {
                    /* class android.support.v4.app.FragmentTransition.AnonymousClass4 */

                    public void run() {
                        ArrayMap<String, View> inSharedElements = FragmentTransition.captureInSharedElements(impl, nameOverrides, sharedElementTransition2, fragments);
                        if (inSharedElements != null) {
                            sharedElementsIn.addAll(inSharedElements.values());
                            sharedElementsIn.add(nonExistentView);
                        }
                        FragmentTransition.callSharedElementStartEnd(inFragment, outFragment, inIsPop, inSharedElements, false);
                        Object obj = sharedElementTransition2;
                        if (obj != null) {
                            impl.swapSharedElementTargets(obj, sharedElementsOut, sharedElementsIn);
                            View inEpicenterView = FragmentTransition.getInEpicenterView(inSharedElements, fragments, enterTransition, inIsPop);
                            if (inEpicenterView != null) {
                                impl.getBoundsOnScreen(inEpicenterView, inEpicenter);
                            }
                        }
                    }
                });
                return sharedElementTransition2;
            }
        }
        return null;
    }

    private static ArrayMap<String, View> captureOutSharedElements(FragmentTransitionImpl impl, ArrayMap<String, String> nameOverrides, Object sharedElementTransition, FragmentContainerTransition fragments) {
        ArrayList<String> names;
        SharedElementCallback sharedElementCallback;
        if (nameOverrides.isEmpty() || sharedElementTransition == null) {
            nameOverrides.clear();
            return null;
        }
        Fragment outFragment = fragments.firstOut;
        ArrayMap<String, View> outSharedElements = new ArrayMap<>();
        impl.findNamedViews(outSharedElements, outFragment.getView());
        BackStackRecord outTransaction = fragments.firstOutTransaction;
        if (fragments.firstOutIsPop) {
            sharedElementCallback = outFragment.getEnterTransitionCallback();
            names = outTransaction.mSharedElementTargetNames;
        } else {
            sharedElementCallback = outFragment.getExitTransitionCallback();
            names = outTransaction.mSharedElementSourceNames;
        }
        outSharedElements.retainAll(names);
        if (sharedElementCallback != null) {
            sharedElementCallback.onMapSharedElements(names, outSharedElements);
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = names.get(i);
                View view = outSharedElements.get(name);
                if (view == null) {
                    nameOverrides.remove(name);
                } else if (!name.equals(ViewCompat.getTransitionName(view))) {
                    nameOverrides.put(ViewCompat.getTransitionName(view), nameOverrides.remove(name));
                }
            }
        } else {
            nameOverrides.retainAll(outSharedElements.keySet());
        }
        return outSharedElements;
    }

    /* access modifiers changed from: private */
    public static ArrayMap<String, View> captureInSharedElements(FragmentTransitionImpl impl, ArrayMap<String, String> nameOverrides, Object sharedElementTransition, FragmentContainerTransition fragments) {
        ArrayList<String> names;
        SharedElementCallback sharedElementCallback;
        String key;
        Fragment inFragment = fragments.lastIn;
        View fragmentView = inFragment.getView();
        if (nameOverrides.isEmpty() || sharedElementTransition == null || fragmentView == null) {
            nameOverrides.clear();
            return null;
        }
        ArrayMap<String, View> inSharedElements = new ArrayMap<>();
        impl.findNamedViews(inSharedElements, fragmentView);
        BackStackRecord inTransaction = fragments.lastInTransaction;
        if (fragments.lastInIsPop) {
            sharedElementCallback = inFragment.getExitTransitionCallback();
            names = inTransaction.mSharedElementSourceNames;
        } else {
            sharedElementCallback = inFragment.getEnterTransitionCallback();
            names = inTransaction.mSharedElementTargetNames;
        }
        if (names != null) {
            inSharedElements.retainAll(names);
            inSharedElements.retainAll(nameOverrides.values());
        }
        if (sharedElementCallback != null) {
            sharedElementCallback.onMapSharedElements(names, inSharedElements);
            for (int i = names.size() - 1; i >= 0; i--) {
                String name = names.get(i);
                View view = inSharedElements.get(name);
                if (view == null) {
                    String key2 = findKeyForValue(nameOverrides, name);
                    if (key2 != null) {
                        nameOverrides.remove(key2);
                    }
                } else if (!name.equals(ViewCompat.getTransitionName(view)) && (key = findKeyForValue(nameOverrides, name)) != null) {
                    nameOverrides.put(key, ViewCompat.getTransitionName(view));
                }
            }
        } else {
            retainValues(nameOverrides, inSharedElements);
        }
        return inSharedElements;
    }

    private static String findKeyForValue(ArrayMap<String, String> map, String value) {
        int numElements = map.size();
        for (int i = 0; i < numElements; i++) {
            if (value.equals(map.valueAt(i))) {
                return map.keyAt(i);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static View getInEpicenterView(ArrayMap<String, View> inSharedElements, FragmentContainerTransition fragments, Object enterTransition, boolean inIsPop) {
        String targetName;
        BackStackRecord inTransaction = fragments.lastInTransaction;
        if (enterTransition == null || inSharedElements == null || inTransaction.mSharedElementSourceNames == null || inTransaction.mSharedElementSourceNames.isEmpty()) {
            return null;
        }
        if (inIsPop) {
            targetName = inTransaction.mSharedElementSourceNames.get(0);
        } else {
            targetName = inTransaction.mSharedElementTargetNames.get(0);
        }
        return inSharedElements.get(targetName);
    }

    private static void setOutEpicenter(FragmentTransitionImpl impl, Object sharedElementTransition, Object exitTransition, ArrayMap<String, View> outSharedElements, boolean outIsPop, BackStackRecord outTransaction) {
        String sourceName;
        if (outTransaction.mSharedElementSourceNames != null && !outTransaction.mSharedElementSourceNames.isEmpty()) {
            if (outIsPop) {
                sourceName = outTransaction.mSharedElementTargetNames.get(0);
            } else {
                sourceName = outTransaction.mSharedElementSourceNames.get(0);
            }
            View outEpicenterView = outSharedElements.get(sourceName);
            impl.setEpicenter(sharedElementTransition, outEpicenterView);
            if (exitTransition != null) {
                impl.setEpicenter(exitTransition, outEpicenterView);
            }
        }
    }

    private static void retainValues(ArrayMap<String, String> nameOverrides, ArrayMap<String, View> namedViews) {
        for (int i = nameOverrides.size() - 1; i >= 0; i--) {
            if (!namedViews.containsKey(nameOverrides.valueAt(i))) {
                nameOverrides.removeAt(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public static void callSharedElementStartEnd(Fragment inFragment, Fragment outFragment, boolean isPop, ArrayMap<String, View> sharedElements, boolean isStart) {
        SharedElementCallback sharedElementCallback;
        if (isPop) {
            sharedElementCallback = outFragment.getEnterTransitionCallback();
        } else {
            sharedElementCallback = inFragment.getEnterTransitionCallback();
        }
        if (sharedElementCallback != null) {
            ArrayList<View> views = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();
            int count = sharedElements == null ? 0 : sharedElements.size();
            for (int i = 0; i < count; i++) {
                names.add(sharedElements.keyAt(i));
                views.add(sharedElements.valueAt(i));
            }
            if (isStart) {
                sharedElementCallback.onSharedElementStart(names, views, null);
            } else {
                sharedElementCallback.onSharedElementEnd(names, views, null);
            }
        }
    }

    /* access modifiers changed from: private */
    public static ArrayList<View> configureEnteringExitingViews(FragmentTransitionImpl impl, Object transition, Fragment fragment, ArrayList<View> sharedElements, View nonExistentView) {
        ArrayList<View> viewList = null;
        if (transition != null) {
            viewList = new ArrayList<>();
            View root = fragment.getView();
            if (root != null) {
                impl.captureTransitioningViews(viewList, root);
            }
            if (sharedElements != null) {
                viewList.removeAll(sharedElements);
            }
            if (!viewList.isEmpty()) {
                viewList.add(nonExistentView);
                impl.addTargets(transition, viewList);
            }
        }
        return viewList;
    }

    /* access modifiers changed from: private */
    public static void setViewVisibility(ArrayList<View> views, int visibility) {
        if (views != null) {
            for (int i = views.size() - 1; i >= 0; i--) {
                views.get(i).setVisibility(visibility);
            }
        }
    }

    private static Object mergeTransitions(FragmentTransitionImpl impl, Object enterTransition, Object exitTransition, Object sharedElementTransition, Fragment inFragment, boolean isPop) {
        boolean z;
        boolean overlap = true;
        if (!(enterTransition == null || exitTransition == null || inFragment == null)) {
            if (isPop) {
                z = inFragment.getAllowReturnTransitionOverlap();
            } else {
                z = inFragment.getAllowEnterTransitionOverlap();
            }
            overlap = z;
        }
        if (overlap) {
            return impl.mergeTransitionsTogether(exitTransition, enterTransition, sharedElementTransition);
        }
        return impl.mergeTransitionsInSequence(exitTransition, enterTransition, sharedElementTransition);
    }

    public static void calculateFragments(BackStackRecord transaction, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isReordered) {
        int numOps = transaction.mOps.size();
        for (int opNum = 0; opNum < numOps; opNum++) {
            addToFirstInLastOut(transaction, transaction.mOps.get(opNum), transitioningFragments, false, isReordered);
        }
    }

    public static void calculatePopFragments(BackStackRecord transaction, SparseArray<FragmentContainerTransition> transitioningFragments, boolean isReordered) {
        if (transaction.mManager.mContainer.onHasView()) {
            for (int opNum = transaction.mOps.size() - 1; opNum >= 0; opNum--) {
                addToFirstInLastOut(transaction, transaction.mOps.get(opNum), transitioningFragments, true, isReordered);
            }
        }
    }

    static boolean supportsTransition() {
        return (PLATFORM_IMPL == null && SUPPORT_IMPL == null) ? false : true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:102:0x012a  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x012f A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:111:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00d2  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x00d6 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0116  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void addToFirstInLastOut(android.support.v4.app.BackStackRecord r22, android.support.v4.app.BackStackRecord.Op r23, android.util.SparseArray<android.support.v4.app.FragmentTransition.FragmentContainerTransition> r24, boolean r25, boolean r26) {
        /*
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r25
            android.support.v4.app.Fragment r10 = r1.fragment
            if (r10 != 0) goto L_0x000d
            return
        L_0x000d:
            int r11 = r10.mContainerId
            if (r11 != 0) goto L_0x0012
            return
        L_0x0012:
            if (r3 == 0) goto L_0x001b
            int[] r4 = android.support.v4.app.FragmentTransition.INVERSE_OPS
            int r5 = r1.cmd
            r4 = r4[r5]
            goto L_0x001d
        L_0x001b:
            int r4 = r1.cmd
        L_0x001d:
            r12 = r4
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 1
            if (r12 == r9) goto L_0x00a8
            r13 = 3
            if (r12 == r13) goto L_0x0079
            r13 = 4
            if (r12 == r13) goto L_0x0057
            r13 = 5
            if (r12 == r13) goto L_0x003c
            r13 = 6
            if (r12 == r13) goto L_0x0079
            r13 = 7
            if (r12 == r13) goto L_0x00a8
            r13 = r4
            r14 = r5
            r15 = r6
            r16 = r7
            goto L_0x00bd
        L_0x003c:
            if (r26 == 0) goto L_0x004d
            boolean r13 = r10.mHiddenChanged
            if (r13 == 0) goto L_0x004b
            boolean r13 = r10.mHidden
            if (r13 != 0) goto L_0x004b
            boolean r13 = r10.mAdded
            if (r13 == 0) goto L_0x004b
            r8 = 1
        L_0x004b:
            r4 = r8
            goto L_0x004f
        L_0x004d:
            boolean r4 = r10.mHidden
        L_0x004f:
            r7 = 1
            r13 = r4
            r14 = r5
            r15 = r6
            r16 = r7
            goto L_0x00bd
        L_0x0057:
            if (r26 == 0) goto L_0x0068
            boolean r13 = r10.mHiddenChanged
            if (r13 == 0) goto L_0x0066
            boolean r13 = r10.mAdded
            if (r13 == 0) goto L_0x0066
            boolean r13 = r10.mHidden
            if (r13 == 0) goto L_0x0066
            r8 = 1
        L_0x0066:
            r6 = r8
            goto L_0x0072
        L_0x0068:
            boolean r13 = r10.mAdded
            if (r13 == 0) goto L_0x0071
            boolean r13 = r10.mHidden
            if (r13 != 0) goto L_0x0071
            r8 = 1
        L_0x0071:
            r6 = r8
        L_0x0072:
            r5 = 1
            r13 = r4
            r14 = r5
            r15 = r6
            r16 = r7
            goto L_0x00bd
        L_0x0079:
            if (r26 == 0) goto L_0x0097
            boolean r13 = r10.mAdded
            if (r13 != 0) goto L_0x0094
            android.view.View r13 = r10.mView
            if (r13 == 0) goto L_0x0094
            android.view.View r13 = r10.mView
            int r13 = r13.getVisibility()
            if (r13 != 0) goto L_0x0094
            float r13 = r10.mPostponedAlpha
            r14 = 0
            int r13 = (r13 > r14 ? 1 : (r13 == r14 ? 0 : -1))
            if (r13 < 0) goto L_0x0094
            r8 = 1
            goto L_0x0095
        L_0x0094:
        L_0x0095:
            r6 = r8
            goto L_0x00a1
        L_0x0097:
            boolean r13 = r10.mAdded
            if (r13 == 0) goto L_0x00a0
            boolean r13 = r10.mHidden
            if (r13 != 0) goto L_0x00a0
            r8 = 1
        L_0x00a0:
            r6 = r8
        L_0x00a1:
            r5 = 1
            r13 = r4
            r14 = r5
            r15 = r6
            r16 = r7
            goto L_0x00bd
        L_0x00a8:
            if (r26 == 0) goto L_0x00ad
            boolean r4 = r10.mIsNewlyAdded
            goto L_0x00b7
        L_0x00ad:
            boolean r13 = r10.mAdded
            if (r13 != 0) goto L_0x00b6
            boolean r13 = r10.mHidden
            if (r13 != 0) goto L_0x00b6
            r8 = 1
        L_0x00b6:
            r4 = r8
        L_0x00b7:
            r7 = 1
            r13 = r4
            r14 = r5
            r15 = r6
            r16 = r7
        L_0x00bd:
            java.lang.Object r4 = r2.get(r11)
            android.support.v4.app.FragmentTransition$FragmentContainerTransition r4 = (android.support.v4.app.FragmentTransition.FragmentContainerTransition) r4
            if (r13 == 0) goto L_0x00d2
            android.support.v4.app.FragmentTransition$FragmentContainerTransition r4 = ensureContainer(r4, r2, r11)
            r4.lastIn = r10
            r4.lastInIsPop = r3
            r4.lastInTransaction = r0
            r8 = r4
            goto L_0x00d3
        L_0x00d2:
            r8 = r4
        L_0x00d3:
            r7 = 0
            if (r26 != 0) goto L_0x0111
            if (r16 == 0) goto L_0x0111
            if (r8 == 0) goto L_0x00e0
            android.support.v4.app.Fragment r4 = r8.firstOut
            if (r4 != r10) goto L_0x00e0
            r8.firstOut = r7
        L_0x00e0:
            android.support.v4.app.FragmentManagerImpl r6 = r0.mManager
            int r4 = r10.mState
            if (r4 >= r9) goto L_0x010b
            int r4 = r6.mCurState
            if (r4 < r9) goto L_0x010b
            boolean r4 = r0.mReorderingAllowed
            if (r4 != 0) goto L_0x010b
            r6.makeActive(r10)
            r9 = 1
            r17 = 0
            r18 = 0
            r19 = 0
            r4 = r6
            r5 = r10
            r20 = r6
            r6 = r9
            r9 = r7
            r7 = r17
            r21 = r8
            r8 = r18
            r1 = r9
            r9 = r19
            r4.moveToState(r5, r6, r7, r8, r9)
            goto L_0x0114
        L_0x010b:
            r20 = r6
            r1 = r7
            r21 = r8
            goto L_0x0114
        L_0x0111:
            r1 = r7
            r21 = r8
        L_0x0114:
            if (r15 == 0) goto L_0x012a
            r4 = r21
            if (r4 == 0) goto L_0x011e
            android.support.v4.app.Fragment r5 = r4.firstOut
            if (r5 != 0) goto L_0x012c
        L_0x011e:
            android.support.v4.app.FragmentTransition$FragmentContainerTransition r8 = ensureContainer(r4, r2, r11)
            r8.firstOut = r10
            r8.firstOutIsPop = r3
            r8.firstOutTransaction = r0
            goto L_0x012d
        L_0x012a:
            r4 = r21
        L_0x012c:
            r8 = r4
        L_0x012d:
            if (r26 != 0) goto L_0x0139
            if (r14 == 0) goto L_0x0139
            if (r8 == 0) goto L_0x0139
            android.support.v4.app.Fragment r4 = r8.lastIn
            if (r4 != r10) goto L_0x0139
            r8.lastIn = r1
        L_0x0139:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentTransition.addToFirstInLastOut(android.support.v4.app.BackStackRecord, android.support.v4.app.BackStackRecord$Op, android.util.SparseArray, boolean, boolean):void");
    }

    private static FragmentContainerTransition ensureContainer(FragmentContainerTransition containerTransition, SparseArray<FragmentContainerTransition> transitioningFragments, int containerId) {
        if (containerTransition != null) {
            return containerTransition;
        }
        FragmentContainerTransition containerTransition2 = new FragmentContainerTransition();
        transitioningFragments.put(containerId, containerTransition2);
        return containerTransition2;
    }

    /* access modifiers changed from: package-private */
    public static class FragmentContainerTransition {
        public Fragment firstOut;
        public boolean firstOutIsPop;
        public BackStackRecord firstOutTransaction;
        public Fragment lastIn;
        public boolean lastInIsPop;
        public BackStackRecord lastInTransaction;

        FragmentContainerTransition() {
        }
    }
}
