import java.util.*;
import java.io.*;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.*;

public class Driver {

    private ArrayList<DriverTransform> driverTransforms = new ArrayList<>();
    public ArrayList<DriverTransform> getDriverTransforms() {
        return driverTransforms;
    }

    private Map<String, Integer> objFilenames = new HashMap<>();

    private String filename;

    public Driver(String filename) {
        this.filename = filename;

        Scanner scanner = null;

        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (scanner != null) {
            String line;
            while (scanner.hasNextLine() && (line = scanner.nextLine()) != null) {
                // Check for comments
                if (!line.trim().startsWith("#")) {

                    DriverTransform dt = new DriverTransform(line);

                    String fn = dt.getObjFilename();
                    if (objFilenames.containsKey(fn)) {
                        int val = objFilenames.get(fn);
                        dt.setObjIndex(val + 1);
                        objFilenames.replace(fn, val + 1);
                    } else {
                        objFilenames.put(fn, 0);
                    }

                    driverTransforms.add(dt);
                }
            }
        }

    }

    public void applyTransforms() {

        for (DriverTransform dt : driverTransforms) {

            RealMatrix  X = getRotation(dt),
                        Y = getScaling(dt),
                        Z = getTranslation(dt);
            dt.setTransformMatrix(Z.multiply(X.multiply(Y)));

            for (ObjFactor factor : dt.getObjFactors()) {
                if (factor instanceof Vertex) {
                    Vertex v = (Vertex)factor;

                    RealMatrix matrix = new Array2DRowRealMatrix(
                            v.getVector().getDataRef());
                    matrix = dt.getTransformMatrix().multiply(matrix);

                    v.setVector(matrix.getColumnVector(0));
                }
            }
        }

    }

    public RealMatrix getRotation(DriverTransform dt) {
        double theta = dt.getTheta();

        Vector3D Wv = new Vector3D(dt.getRotateX(), dt.getRotateY(),
                dt.getRotateZ());
        Wv = Wv.normalize();

        double[] tempMv = { Math.abs(Wv.getX()), Math.abs(Wv.getY()),
                Math.abs(Wv.getZ()) };
        int minIndex = 0;
        for (int i = 0; i < 3; ++i) {
            if (tempMv[i] <= tempMv[minIndex]) {
                minIndex = i;
            }
        }
        tempMv[minIndex] = 1.0;
        Vector3D Mv = new Vector3D(tempMv);

        Vector3D Uv = Wv.crossProduct(Mv);
        Uv = Uv.normalize();
        Vector3D Vv = Wv.crossProduct(Uv);
        Vv = Vv.normalize();

        RealMatrix RM = new Array2DRowRealMatrix(new double[][] {
                { Uv.getX(), Uv.getY(), Uv.getZ(), 0.0 },
                { Vv.getX(), Vv.getY(), Vv.getZ(), 0.0 },
                { Wv.getX(), Wv.getY(), Wv.getZ(), 0.0 },
                { 0.0, 0.0, 0.0, 1.0 }
        });
        RealMatrix RMt = RM.transpose();
        //RealMatrix RMRMt = RMt.multiply(RM);

        double rad = theta * (3.1415926535897932384626433833 / 180.0);
        double ca = Math.cos(rad);
        double sa = Math.sin(rad);
        RealMatrix RMz = new Array2DRowRealMatrix(new double[][] {
                { ca, -sa, 0.0, 0.0 },
                { sa, ca, 0.0, 0.0 },
                { 0.0, 0.0, 1.0, 0.0 },
                { 0.0, 0.0, 0.0, 1.0 }
        });
        RealMatrix RT = RMt.multiply(RMz.multiply(RM));
        return RT;
    }

    public RealMatrix getScaling(DriverTransform dt) {
        return new Array2DRowRealMatrix(new double[][] {
                { dt.getScale(), 0.0, 0.0, 0.0 },
                { 0.0, dt.getScale(), 0.0, 0.0 },
                { 0.0, 0.0, dt.getScale(), 0.0 },
                { 0.0, 0.0, 0.0, 1.0 }
        });
    }

    public RealMatrix getTranslation(DriverTransform dt) {
        return new Array2DRowRealMatrix(new double[][] {
                { 1.0, 0.0, 0.0, dt.getTranslateX() },
                { 0.0, 1.0, 0.0, dt.getTranslateY() },
                { 0.0, 0.0, 1.0, dt.getTranslateZ() },
                { 0.0, 0.0, 0.0, 1.0 }
        });
    }

    public void writeTransformsToFile() {

        String folderPath = filename.substring(0, filename.lastIndexOf('.')) + "/";
        File fileFolder = new File(folderPath);
        fileFolder.mkdir();

        for (DriverTransform dt : driverTransforms) {

            String indexExt = String.format("%02d", dt.getObjIndex());
            String objFilename = dt.getObjFilename().substring(0, dt.getObjFilename().lastIndexOf('.'));

            String objPath = String.format("%s/%s_mw%s.obj", fileFolder.getAbsolutePath(),
                objFilename, indexExt);
            File f = new File(objPath);
            writeObjFile(f, dt);

            objPath = String.format("%s/%s_transform_mw%s.txt", fileFolder.getAbsolutePath(),
                objFilename, indexExt);
            f = new File(objPath);
            writeTransformFile(f, dt);
        }

    }

    private void writeObjFile(File f, DriverTransform dt) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);

            for (ObjFactor fac : dt.getObjFactors()) {
                fw.write(fac.toString() + "\n");
            }

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTransformFile(File f, DriverTransform dt) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(f);

            fw.write("# Transformation matrix\n");
            RealMatrix matrix = dt.getTransformMatrix();
            for (int i = 0; i < matrix.getColumnDimension(); ++i) {
                for (int j = 0; j < matrix.getRowDimension(); ++j) {
                    fw.write(String.format("%.3f ", matrix.getEntry(i, j)));
                }
                fw.write("\n");
            }
            fw.write("\n");

            fw.write("# Inverse transformation matrix\n");
            RealMatrix inverseMatrix = MatrixUtils.inverse(dt.getTransformMatrix());
            for (int i = 0; i < inverseMatrix.getColumnDimension(); ++i) {
                for (int j = 0; j < inverseMatrix.getRowDimension(); ++j) {
                    fw.write(String.format("%.3f ", inverseMatrix.getEntry(i, j)));
                }
                fw.write("\n");
            }
            fw.write("\n");

            fw.write("# Sum absolute translations from original to transformed\n");
            double sum = 0.0;
            for (ObjFactor factor : dt.getObjFactors()) {
                if (factor instanceof Vertex) {
                    Vertex v = (Vertex)factor;
                    sum += v.lastTranslation();
                }
            }
            fw.write(String.format("%.10f\n", sum));
            fw.write("\n");

            fw.write("# Sum absolute translations from original to transformed to \"original\"\n");
            fw.write(String.format("%.10f", 0.0));
            fw.write("\n");

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
