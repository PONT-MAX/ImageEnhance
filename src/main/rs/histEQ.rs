#pragma version(1)
#pragma rs_fp_relaxed
#pragma rs java_package_name(com.android.rs.histEqRs)

#include "rs_debug.rsh"


//The histogram in a array
int32_t hist_array[256];

//The transformfunction
float T[256];

//Method to keep the result between 0 and 1
// int the YOV space
// Static beacuse returning value to other function
static float bound (float val) {
    float m = fmax(0.0f, val);
    return fmin(1.0f, m);
}

//__attribute__((kernel)) is type of function
// The root function will be called from the api when we call the forEach_root()
// Our main function?

uchar4 __attribute__((kernel)) root(uchar4 in, uint32_t x, uint32_t y) {
    //Convert input uchar4 to float4
    float4 f4 = rsUnpackColor8888(in);

    //Get YUV channels values
    // Same as HSV-ish
    float Y = 0.299f * f4.r + 0.587f * f4.g + 0.114f * f4.b;
    float U = ((0.492f * (f4.b - Y))+1)/2;
    float V = ((0.877f * (f4.r - Y))+1)/2;

    //Get Y value between 0 and 255 (included)
    int32_t val = Y * 255;
    //Increment histogram for that value
    rsAtomicInc(&hist_array[val]);

    //Put the values in the output uchar4, note that we keep the alpha value
    //Packs three or four floating point RGBA values into a uchar4.
    // Renderscript reads typs uchar we want int/float
    return rsPackColorTo8888(Y, U, V, f4.a);
}

uchar4 __attribute__((kernel)) intensTransToRGB(uchar4 in, uint32_t x, uint32_t y) {
    //Convert input uchar4 to float4
    float4 f4 = rsUnpackColor8888(in);

    //Get Y value
    float Y = f4.r;
    //Get Y value between 0 and 255 (included)
    int32_t val = Y * 255;

    //Get Y new value in the map array
    // From the Transfom function array
    Y = T[val];

    //Get value for U and V channel (back to their original values)
    float U = (2*f4.g)-1;
    float V = (2*f4.b)-1;

    //Compute values for red, green and blue channels
    float red = bound(Y + 1.14f * V);
    float green = bound(Y - 0.395f * U - 0.581f * V);
    float blue = bound(Y + 2.033f * U);

    //Put the values in the output uchar4
    return rsPackColorTo8888(red, green, blue, f4.a);
}

// This method is automatically called when creating the script in java.
// It initializes the arrays with zeros.
void init() {
    //init the array with zeros
    for (int i = 0; i < 256; i++) {
        hist_array[i] = 0;
        T[i] = 0.0f;
    }
}

void createTransformFunctionArray() {
    //create map for y
    float sum = 0;
    for (int i = 0; i < 256; i++) {
        sum += histo[i];
        T[i] = sum / (size);
    }
}