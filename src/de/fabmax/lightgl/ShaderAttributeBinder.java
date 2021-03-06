package de.fabmax.lightgl;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.Buffer;
import java.nio.FloatBuffer;

/**
 * A ShaderAttributeBinder binds a buffer with vertex attribute data to a Shader. The buffer can be
 * a {@link java.nio.Buffer} or a GL buffer objects.
 * 
 * @author fabmax
 * 
 */
public abstract class ShaderAttributeBinder {

    /**
     * Creates a ShaderAttributeBinder for a {@link java.nio.FloatBuffer}. The type is set to GL_FLOAT
     * and the offset to 0. Use {@link ShaderAttributeBinder#setType(int)} and
     * {@link ShaderAttributeBinder#setOffset(int)} to change them.
     * 
     * @param buffer
     *            vertex attribute buffer
     * @param size
     *            number of primitives for this attribute
     * @param stride
     *            buffer stride as number of bytes
     * @return ShaderAttributeBinder for use with a {@link Mesh}
     */
    public static ShaderAttributeBinder createFloatBufferBinder(FloatBuffer buffer, int size, int stride) {
        ShaderAttributeBinder binder = new FloatBufferAttributeBinder(buffer);
        binder.setStride(stride);
        binder.setSize(size);
        return binder;
    }

    /**
     * Creates a ShaderAttributeBinder for a GL Vertex buffer object. The type is set to GL_FLOAT and the offset
     * to 0. Use {@link ShaderAttributeBinder#setType(int)} and
     * {@link ShaderAttributeBinder#setOffset(int)} to change them.
     * 
     * @param ptr
     *            GL buffer pointer
     * @param size
     *            number of primitives for this attribute
     * @param stride
     *            buffer stride as number of bytes
     * @return ShaderAttributeBinder for use with a {@link Mesh}
     */
    public static ShaderAttributeBinder createVboBufferBinder(int ptr, int size, int stride) {
        ShaderAttributeBinder binder = new VboBufferAttributeBinder(ptr);
        binder.setStride(stride);
        binder.setSize(size);
        return binder;
    }

    /** Buffer data type. */
    protected int mType = GL_FLOAT;
    /** Buffer stride in bytes. */
    protected int mStride;
    /** Number of primitives per element. */
    protected int mSize;
    /** Buffer offset in bytes. */
    protected int mOffset = 0;

    /**
     * Returns the data type of the buffer.
     * 
     * @return the data type of the buffer
     */
    public int getType() {
        return mType;
    }

    /**
     * Sets the data type of the buffer. By default the data type is set to GL_FLOAT.
     * 
     * @param type
     *            the data type to set
     */
    public void setType(int type) {
        this.mType = type;
    }

    /**
     * Returns the buffer stride in bytes.
     * 
     * @return the buffer stride in bytes
     */
    public int getStride() {
        return mStride;
    }

    /**
     * Sets the buffer stride in bytes.
     * 
     * @param stride
     *            the buffer stride to set
     */
    public void setStride(int stride) {
        this.mStride = stride;
    }

    /**
     * Returns the number of primitives per element.
     * 
     * @return the number of primitives per element
     */
    public int getSize() {
        return mSize;
    }

    /**
     * Sets the number of primitives per element.
     * 
     * @param size
     *            the number of primitives per element
     */
    public void setSize(int mSize) {
        this.mSize = mSize;
    }

    /**
     * Returns the attribute offset in the buffer.
     * 
     * @return the attribute offset in the buffer.
     */
    public int getOffset() {
        return mOffset;
    }

    /**
     * Sets the attribute offset in the buffer.
     * 
     * @param offset
     *            the attribute offset to set
     */
    public void setOffset(int offset) {
        this.mOffset = offset;
    }

    /**
     * Is called by the used shader to bind a vertex attribute buffer to the specified target.
     * 
     * @param target
     *            the buffer target index as used in glVertexAttribPointer()
     */
    public abstract boolean bindAttribute(int target);

    /**
     * ShaderAttributeBinder implementation for {@link java.nio.Buffer} binding.
     * 
     * @author fabmax
     * 
     */
    private static class FloatBufferAttributeBinder extends ShaderAttributeBinder {

        private final Buffer mBuffer;

        /**
         * Creates a ShaderAttributeBinder for a Buffer.
         * 
         * @param buffer
         *            the buffer to use
         */
        private FloatBufferAttributeBinder(Buffer buffer) {
            mBuffer = buffer;
        }

        /**
         * Bind buffer to shader attribute.
         */
        @Override
        public boolean bindAttribute(int target) {
            mBuffer.position(mOffset);
            glVertexAttribPointer(target, mSize, mType, false, mStride, mBuffer);
            return true;
        }
    }

    /**
     * ShaderAttributeBinder implementation for GL VBO binding.
     * 
     * @author fabmax
     * 
     */
    private static class VboBufferAttributeBinder extends ShaderAttributeBinder {

        private final int mBuffer;

        /**
         * Creates a ShaderAttributeBinder for a GL VBO.
         * 
         * @param buffer
         *            the buffer to use
         */
        private VboBufferAttributeBinder(int buffer) {
            mBuffer = buffer;
        }

        /**
         * Bind buffer to shader attribute.
         */
        @Override
        public boolean bindAttribute(int target) {
            glBindBuffer(GL_ARRAY_BUFFER, mBuffer);
            glVertexAttribPointer(target, mSize, mType, false, mStride, mOffset * 4);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            return true;
        }

    }
}
