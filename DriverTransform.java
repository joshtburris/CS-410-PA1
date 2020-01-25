import org.apache.commons.math3.linear.*;
import java.util.*;
import java.io.*;

public class DriverTransform {

    private String objType;
    public String getObjType() { return objType; }
    public void setObjType(String objType) { this.objType = objType; }

    private double rotateX;
    public double getRotateX() { return rotateX; }
    public void setRotateX(double wx) { rotateX = wx; }

    private double rotateY;
    public double getRotateY() { return rotateY; }
    public void setRotateY(double wy) { rotateY = wy; }

    private double rotateZ;
    public double getRotateZ() { return rotateZ; }
    public void setRotateZ(double wz) { rotateZ = wz; }

    private double theta;
    public double getTheta() { return theta; }
    public void setTheta(double theta) { this.theta = theta; }

    private double scale;
    public double getScale() { return scale; }
    public void setScale(double scale) { this.scale = scale; }

    private double translateX;
    public double getTranslateX() { return translateX; }
    public void setTranslateX(double tx) { translateX = tx; }

    private double translateY;
    public double getTranslateY() { return translateY; }
    public void setTranslateY(double ty) { translateY = ty; }

    private double translateZ;
    public double getTranslateZ() { return translateZ; }
    public void setTranslateZ(double tz) { translateZ = tz; }

    private String objFilename;
    public String getObjFilename() { return objFilename; }
    public void setObjFilename(String objFilename) { this.objFilename = objFilename; }

    private int objIndex;
    public int getObjIndex() { return objIndex; }
    public void setObjIndex(int objIndex) { this.objIndex = objIndex; }

    private ArrayList<ObjFactor> objFactors = new ArrayList<>();
    public ArrayList<ObjFactor> getObjFactors() { return objFactors; }

    private RealMatrix transformMatrix = new Array2DRowRealMatrix(
            new double[][] { { 1.0, 0.0, 0.0, 0.0 },
                             { 0.0, 1.0, 0.0, 0.0 },
                             { 0.0, 0.0, 1.0, 0.0 },
                             { 0.0, 0.0, 0.0, 1.0 } });
    public RealMatrix getTransformMatrix() { return transformMatrix; }
    public void setTransformMatrix(RealMatrix matrix) { this.transformMatrix = matrix; }


    public DriverTransform(String line) {
        Scanner scanner = new Scanner(line);
        objType = scanner.next();
        rotateX = scanner.nextDouble();
        rotateY = scanner.nextDouble();
        rotateZ = scanner.nextDouble();
        theta = scanner.nextDouble();
        scale = scanner.nextDouble();
        translateX = scanner.nextDouble();
        translateY = scanner.nextDouble();
        translateZ = scanner.nextDouble();
        objFilename = scanner.next();
        objIndex = 0;
        loadObjFactors();
    }

    public DriverTransform(String line, int objIndex) {
        this(line);
        this.objIndex = objIndex;
    }

    private void loadObjFactors() {
        try {

            Scanner scan = new Scanner(new File(objFilename));
            while (scan.hasNext()) {
                String next = scan.next();
                if (next.compareTo("v") == 0) {
                    objFactors.add(new Vertex(scan.nextDouble(),
                            scan.nextDouble(), scan.nextDouble()));
                } else if (next.compareTo("vn") == 0) {
                    scan.nextLine();
                } else {
                    objFactors.add(new ObjFactor(next + " " + scan.nextLine()));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
