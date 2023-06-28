package com.itsmagic.code.jeditor.lsp.base_services;

import android.os.Binder;

public abstract class BaseLSPBinder<T extends BaseLSPService> extends Binder {
    public abstract T getInstance();
}