package com.itsmagic.code.jeditor.utils;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

public class UriUtils {
	public static DocumentFile[] listFileTree(Context context, Uri path) {
		DocumentFile fromTreeUri = DocumentFile.fromTreeUri(context, path);
		DocumentFile[] documentFiles = fromTreeUri.listFiles();

		return documentFiles;
	}
}
