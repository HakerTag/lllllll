package kotlin.io;

import java.io.File;
import java.io.IOException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public class FileSystemException extends IOException {
    private final File file;
    private final File other;
    private final String reason;

    public final File getFile() {
        return this.file;
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public /* synthetic */ FileSystemException(File file2, File file3, String str, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(file2, (i & 2) != 0 ? null : file3, (i & 4) != 0 ? null : str);
    }

    public final File getOther() {
        return this.other;
    }

    public final String getReason() {
        return this.reason;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public FileSystemException(File file2, File file3, String str) {
        super(ExceptionsKt.constructMessage(file2, file3, str));
        Intrinsics.checkParameterIsNotNull(file2, "file");
        this.file = file2;
        this.other = file3;
        this.reason = str;
    }
}
