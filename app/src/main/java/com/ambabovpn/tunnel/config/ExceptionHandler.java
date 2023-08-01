package com.ambabovpn.tunnel.config;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import com.ambabovpn.tunnel.ExceptionActivity;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHandler implements UncaughtExceptionHandler {
    private final Context myContext;
    public ExceptionHandler(Context activity) {
        this.myContext = activity;
    }
    public void uncaughtException(Thread thread, Throwable th) {
        Writer stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("************ APPLICATION ERROR ************\n\n");
        stringBuilder.append(stringWriter.toString());
        stringBuilder.append("\n************ DEVICE INFORMATION ***********\n");
        stringBuilder.append("Brand: ");
        stringBuilder.append(Build.BRAND);
        stringBuilder.append("\n");
        stringBuilder.append("Device: ");
        stringBuilder.append(Build.DEVICE);
        stringBuilder.append("\n");
        stringBuilder.append("Model: ");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append("\n");
        stringBuilder.append("Id: ");
        stringBuilder.append(Build.ID);
        stringBuilder.append("\n");
        stringBuilder.append("Product: ");
        stringBuilder.append(Build.PRODUCT);
        stringBuilder.append("\n");
        stringBuilder.append("\n************ FIRMWARE ************\n");
        stringBuilder.append("SDK: ");
        stringBuilder.append(VERSION.SDK);
        stringBuilder.append("\n");
        stringBuilder.append("Release: ");
        stringBuilder.append(VERSION.RELEASE);
        stringBuilder.append("\n");
        stringBuilder.append("Incremental: ");
        stringBuilder.append(VERSION.INCREMENTAL);
        stringBuilder.append("\n");
        stringBuilder.append("Security Patch: ");
        stringBuilder.append(VERSION.SECURITY_PATCH);
        stringBuilder.append("\n");
        try {
            Intent intent = new Intent(this.myContext, ExceptionActivity.class);
            intent.putExtra("error", stringBuilder.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.myContext.startActivity(intent);
            Process.killProcess(Process.myPid());
            System.exit(10);
        } catch (Throwable e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}



