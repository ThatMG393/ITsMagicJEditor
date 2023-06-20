package com.itsmagic.code.editor.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.net.Uri;
import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import com.anggrayudi.storage.SimpleStorageHelper;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.anggrayudi.storage.file.FileFullPath;
import com.anggrayudi.storage.file.StorageId;
import com.anggrayudi.storage.file.StorageType;
import org.jetbrains.annotations.NotNull;

public class StorageUtils {
	private static SimpleStorageHelper storageHelper = null;
	public static void setupStorageHelper(AppCompatActivity activity) {
		storageHelper = new SimpleStorageHelper(activity);
	}
	
	public static void askForDirectoryAccess(@NotNull AppCompatActivity activity, @NotNull String path, @NotNull int requestCode, @NotNull OnAllowFolderAccess listener) {
		
		storageHelper.setOnStorageAccessGranted((rCode, root) -> {
			String absolutePath = DocumentFileUtils.getAbsolutePath(root, activity);
			listener.onAllowFolderAccess(rCode, root);
			SharedPreferenceUtils.getInstance().putBool(absolutePath, true);

			// Toast.makeText(context, "Access granted for folder: " + absolutePath, Toast.LENGTH_LONG).show();
			return null;
		});

		FileFullPath fullPath = null;
		if (SDK_INT >= 30) { fullPath = new FileFullPath(activity, StorageType.EXTERNAL, path); }
		else if (SDK_INT <= 29) { fullPath = new FileFullPath(activity, StorageId.PRIMARY, path); }
		
		if (!SharedPreferenceUtils.getInstance().getBool(fullPath.getAbsolutePath())) {
			storageHelper.requestStorageAccess(
				requestCode,
				fullPath,
				StorageType.EXTERNAL,
				 path
			);
		} else {
			listener.onAllowFolderAccess(requestCode, fullPath.toDocumentUri(activity));
		}
	}
	
	public interface OnAllowFolderAccess {
		public default void onAllowFolderAccess(int rCode, Uri path) { }
		public default void onAllowFolderAccess(int rCode, String path) { }
		public default void onAllowFolderAccess(int rCode, DocumentFile path) { }
	}

}
