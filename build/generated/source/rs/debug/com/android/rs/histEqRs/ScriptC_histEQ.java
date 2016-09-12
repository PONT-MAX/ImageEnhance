/*
 * Copyright (C) 2011-2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is auto-generated. DO NOT MODIFY!
 * The source Renderscript file: /Users/Andreas/Skola/ImageEnhance/src/main/rs/histEQ.rs
 */

package com.android.rs.histEqRs;

import android.support.v8.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_histEQ extends ScriptC {
    private static final String __rs_resource_name = "histeq";
    // Constructor
    public  ScriptC_histEQ(RenderScript rs) {
        this(rs,
             rs.getApplicationContext().getResources(),
             rs.getApplicationContext().getResources().getIdentifier(
                 __rs_resource_name, "raw",
                 rs.getApplicationContext().getPackageName()));
    }

    public  ScriptC_histEQ(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
        __I32 = Element.I32(rs);
        __F32 = Element.F32(rs);
        __U8_4 = Element.U8_4(rs);
    }

    private Element __F32;
    private Element __I32;
    private Element __U8_4;
    private FieldPacker __rs_fp_F32;
    private FieldPacker __rs_fp_I32;
    private final static int mExportVarIdx_hist_array = 0;
    private int[] mExportVar_hist_array;
    public synchronized void set_hist_array(int[] v) {
        mExportVar_hist_array = v;
        FieldPacker fp = new FieldPacker(1024);
        for (int ct1 = 0; ct1 < 256; ct1++) {
            fp.addI32(v[ct1]);
        }

        int []__dimArr = new int[1];
        __dimArr[0] = 256;
        setVar(mExportVarIdx_hist_array, fp, __I32, __dimArr);
    }

    public int[] get_hist_array() {
        return mExportVar_hist_array;
    }

    public Script.FieldID getFieldID_hist_array() {
        return createFieldID(mExportVarIdx_hist_array, null);
    }

    private final static int mExportVarIdx_size = 1;
    private int mExportVar_size;
    public synchronized void set_size(int v) {
        setVar(mExportVarIdx_size, v);
        mExportVar_size = v;
    }

    public int get_size() {
        return mExportVar_size;
    }

    public Script.FieldID getFieldID_size() {
        return createFieldID(mExportVarIdx_size, null);
    }

    private final static int mExportVarIdx_T = 2;
    private float[] mExportVar_T;
    public synchronized void set_T(float[] v) {
        mExportVar_T = v;
        FieldPacker fp = new FieldPacker(1024);
        for (int ct1 = 0; ct1 < 256; ct1++) {
            fp.addF32(v[ct1]);
        }

        int []__dimArr = new int[1];
        __dimArr[0] = 256;
        setVar(mExportVarIdx_T, fp, __F32, __dimArr);
    }

    public float[] get_T() {
        return mExportVar_T;
    }

    public Script.FieldID getFieldID_T() {
        return createFieldID(mExportVarIdx_T, null);
    }

    private final static int mExportForEachIdx_root = 0;
    public Script.KernelID getKernelID_root() {
        return createKernelID(mExportForEachIdx_root, 59, null, null);
    }

    public void forEach_root(Allocation ain, Allocation aout) {
        forEach_root(ain, aout, null);
    }

    public void forEach_root(Allocation ain, Allocation aout, Script.LaunchOptions sc) {
        // check ain
        if (!ain.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        // check aout
        if (!aout.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        Type t0, t1;        // Verify dimensions
        t0 = ain.getType();
        t1 = aout.getType();
        if ((t0.getCount() != t1.getCount()) ||
            (t0.getX() != t1.getX()) ||
            (t0.getY() != t1.getY()) ||
            (t0.getZ() != t1.getZ()) ||
            (t0.hasFaces()   != t1.hasFaces()) ||
            (t0.hasMipmaps() != t1.hasMipmaps())) {
            throw new RSRuntimeException("Dimension mismatch between parameters ain and aout!");
        }

        forEach(mExportForEachIdx_root, ain, aout, null, sc);
    }

    private final static int mExportForEachIdx_intensTransToRGB = 1;
    public Script.KernelID getKernelID_intensTransToRGB() {
        return createKernelID(mExportForEachIdx_intensTransToRGB, 59, null, null);
    }

    public void forEach_intensTransToRGB(Allocation ain, Allocation aout) {
        forEach_intensTransToRGB(ain, aout, null);
    }

    public void forEach_intensTransToRGB(Allocation ain, Allocation aout, Script.LaunchOptions sc) {
        // check ain
        if (!ain.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        // check aout
        if (!aout.getType().getElement().isCompatible(__U8_4)) {
            throw new RSRuntimeException("Type mismatch with U8_4!");
        }
        Type t0, t1;        // Verify dimensions
        t0 = ain.getType();
        t1 = aout.getType();
        if ((t0.getCount() != t1.getCount()) ||
            (t0.getX() != t1.getX()) ||
            (t0.getY() != t1.getY()) ||
            (t0.getZ() != t1.getZ()) ||
            (t0.hasFaces()   != t1.hasFaces()) ||
            (t0.hasMipmaps() != t1.hasMipmaps())) {
            throw new RSRuntimeException("Dimension mismatch between parameters ain and aout!");
        }

        forEach(mExportForEachIdx_intensTransToRGB, ain, aout, null, sc);
    }

    private final static int mExportFuncIdx_createTransformFunctionArray = 0;
    public void invoke_createTransformFunctionArray() {
        invoke(mExportFuncIdx_createTransformFunctionArray);
    }

}

