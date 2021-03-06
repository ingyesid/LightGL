package de.fabmax.lightgl;

import android.opengl.GLU;
import android.opengl.Matrix;
import android.view.MotionEvent;

/**
 * Base class for arbitrary camera implementations.
 * 
 * @author fabmax
 * 
 */
public abstract class Camera {

    /** Camera position */
    protected float mEyeX = 0, mEyeY = 0, mEyeZ = 10;

    /** Camera look at position */
    protected float mLookAtX = 0, mLookAtY = 0, mLookAtZ = 0;

    /** Camera up direction */
    protected float mUpX = 0, mUpY = 1, mUpZ = 0;
    
    /** Matrix recalculation flag */
    protected boolean mDirty = true;

    /** Camera transformation matrices */
    protected float[] mViewMatrix = new float[16];
    protected float[] mProjMatrix = new float[16];

    /**
     * Sets the camera position.
     */
    public void setPosition(float x, float y, float z) {
        mEyeX = x;
        mEyeY = y;
        mEyeZ = z;
        mDirty = true;
    }

    /**
     * Sets the position the camera looks at.
     */
    public void setLookAt(float x, float y, float z) {
        mLookAtX = x;
        mLookAtY = y;
        mLookAtZ = z;
        mDirty = true;
    }

    /**
     * Sets the camera up direction.
     */
    public void setUpDirection(float x, float y, float z) {
        mUpX = x;
        mUpY = y;
        mUpZ = z;
        mDirty = true;
    }
    
    /**
     * Sets the dirty flag so that the camera matrices are recomputed on next call of setup().
     */
    protected void setDirty() {
        mDirty = true;
    }
    
    /**
     * Sets the view and projection matrices of the {@link GfxState} according to the current camera
     * settings.
     * 
     * @param state the GfxState to set up
     */
    public void setup(GfxState state) {
        checkMatrices();
        System.arraycopy(mProjMatrix, 0, state.getProjectionMatrix(), 0, 16);
        System.arraycopy(mViewMatrix, 0, state.getViewMatrix(), 0, 16);
        state.matrixUpdate();
    }
    
    /**
     * Recomputes view and projection matrices if needed.
     */
    private void checkMatrices() {
        if (mDirty) {
            mDirty = false;
            computeProjectionMatrix(mProjMatrix);
            computeViewMatrix(mViewMatrix);
        }
    }
    
    /**
     * Computes a {@link Ray} for the given screen coordinates. The Ray has the same origin and
     * direction as the virtual camera ray at that pixel. E.g. (x, y) can come from a
     * {@link MotionEvent} and the computed Ray can be used to pick scene objects. Notice that this
     * function uses the projection and view matrices from {@link GfxState} so these must be valid
     * in order for this function to work. Use {@link GfxState#setCamera(Camera)} to explicitly set
     * the camera matrices.
     * 
     * @see GfxState#getViewport()
     * 
     * @param viewport
     *            Viewport dimensions
     * @param x
     *            X screen coordinate in pixels
     * @param y
     *            Y screen coordinate in pixels
     * @param result
     *            Ray representing the camera Ray at the specified pixel
     */
    public void getPickRay(int[] viewport, float x, float y, Ray result) {
        float yInv = viewport[3] - y;
        GLU.gluUnProject(x, yInv, 0.0f, mViewMatrix, 0, mProjMatrix, 0, viewport, 0, result.origin, 0);
        GLU.gluUnProject(x, yInv, 1.0f, mViewMatrix, 0, mProjMatrix, 0, viewport, 0, result.direction, 0);
        
        // only took me a hour to figure out that the Android gluUnProject version does not divide
        // the resulting coordinates by w...
        float s = 1.0f / result.origin[3];
        result.origin[0] *= s;
        result.origin[1] *= s;
        result.origin[2] *= s;
        result.origin[3] = 1.0f;
        
        s = 1.0f / result.direction[3];
        result.direction[0] *= s;
        result.direction[1] *= s;
        result.direction[2] *= s;
        result.direction[3] = 0.0f;

        result.direction[0] -= result.origin[0];
        result.direction[1] -= result.origin[1];
        result.direction[2] -= result.origin[2];
    }

    /**
     * Computes the view matrix for this camera. This method is called by GfxEngine for the active
     * camera every time before a frame is rendered. The default implementation calls
     * {@link Matrix#setLookAtM(float[], int, float, float, float, float, float, float, float, float, float)}
     * with the parameters for this camera.
     * 
     * @param viewMBuf
     *            16 element array where the view matrix is stored in
     */
    public void computeViewMatrix(float[] viewMBuf) {
        Matrix.setLookAtM(viewMBuf, 0, mEyeX, mEyeY, mEyeZ, mLookAtX, mLookAtY, mLookAtZ, mUpX,
                mUpY, mUpZ);
    }

    /**
     * Computes the projection matrix for this camera. This method is called by GfxEngine for the
     * active camera every time before a frame is rendered.
     * 
     * @param projMBuf
     *            16 element array where the projection matrix is stored in
     */
    public abstract void computeProjectionMatrix(float[] projMBuf);
}
