public class Modeltoworld {

    public static double[][] I3 = { { 1.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0},
            { 0.0, 0.0, 1.0 } };
    public static double[][] I4 = { { 1.0, 0.0, 0.0, 0.0 }, { 0.0, 1.0, 0.0, 0.0 },
            { 0.0, 0.0, 1.0, 0.0 }, { 0.0, 0.0, 0.0, 1.0 } };

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Incorrect number of arguments. We can only accept one.");
            System.exit(1);
        }

        Driver driver = new Driver(args[0]);
        driver.applyTransforms();
        driver.writeTransformsToFile();
    }

}
