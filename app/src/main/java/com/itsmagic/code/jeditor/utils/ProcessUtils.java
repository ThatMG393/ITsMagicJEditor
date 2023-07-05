package com.itsmagic.code.jeditor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class ProcessUtils {
    public static Process runExecutable(String... cmd) throws IOException {
        ProcessBuilder processB = new ProcessBuilder(cmd);
        Process process = processB.start();

        return process;
    }
}
