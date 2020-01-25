import org.apache.commons.math3.linear.*;

public class Vertex extends ObjFactor {

    private ArrayRealVector vec;
    public ArrayRealVector getVector() { return vec; }
    public void setVector(RealVector vec) {
        lastTranslation = 0.0;
        for (int i = 0; i < 4; ++i) {
            lastTranslation += Math.abs(this.vec.getEntry(i) - vec.getEntry(i));
        }
        this.vec = new ArrayRealVector(vec);
    }
    public Array2DRowRealMatrix getMatrix() {
        return new Array2DRowRealMatrix(vec.getDataRef());
    }

    private double lastTranslation;
    public double lastTranslation() { return lastTranslation; }


    public Vertex(double x, double y, double z) {
        vec = new ArrayRealVector(new double[] { x, y, z, 1.0 });
    }

    public String toString() {
        return String.format("v %.6f %.6f %.6f", vec.getEntry(0),
                vec.getEntry(1), vec.getEntry(2));
    }

}
