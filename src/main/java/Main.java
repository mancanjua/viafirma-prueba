public class Main {

    public static void main(String[] args) {
        String path = null;
        boolean recursive = false;

        if(args.length == 1) {
            if(args[0].equals("true") || args[0].equals("false")) {
                recursive = Boolean.parseBoolean(args[0]);
            } else {
                path = args[0];
            }
        } else if(args.length > 1) {
            path = args[0];
            if(args[1].equals("true") || args[1].equals("false")) {
                recursive = Boolean.parseBoolean(args[1]);
            }
        }

        if(System.getProperty("directory") != null && !System.getProperty("directory").isEmpty()) {
            path = System.getProperty("directory");
        }
        if(System.getProperty("recursive") != null && !System.getProperty("recursive").isEmpty()) {
            recursive = Boolean.parseBoolean(System.getProperty("recursive"));
        }

        PDFTools tool;
        if(path == null) {
            tool = new PDFTools();
        } else {
            tool = new PDFTools(path);
        }

        tool.addPagesDirectory(recursive);
    }

}
