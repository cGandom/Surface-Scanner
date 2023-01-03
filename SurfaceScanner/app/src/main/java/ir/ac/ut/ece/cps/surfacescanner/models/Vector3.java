package ir.ac.ut.ece.cps.surfacescanner.models;

public class Vector3 {

    private float x, y, z;

    Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 update(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Vector3 clear() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        return this;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
}