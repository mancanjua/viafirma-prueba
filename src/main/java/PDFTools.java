import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PDFTools {

    private final File directory;
    //private static final Log LOG = LogFactory.getLog(PDFTools.class);
    private static final Logger LOG = LogManager.getLogger(PDFTools.class);

    public PDFTools(String path) {
        this.directory = new File(path);
        LOG.info("Directorio establecido: " + this.directory.getPath());
    }

    public PDFTools() {
        this(System.getenv("VIAFIRMA_PATH"));
    }

    public void addPagesDirectory(boolean recursive) {
        try {
            if(Files.notExists(this.directory.toPath())) {
                LOG.error("El directorio especificado no existe.");
                return;
            }

            if(!Files.isWritable(this.directory.toPath())) {
                LOG.error("Permisos insuficientes de escritura en el directorio.");
                return;
            }
        } catch (SecurityException e) {
            LOG.error("Permisos insuficientes para acceder al directorio.");
            LOG.error("SecurityException: ", e);
        }

        FileFilter pdfFilter = dir -> dir.isFile() && dir.getPath().toLowerCase().matches("^.*\\.pdf$");

        if(pdfFilter.accept(this.directory)) {
            this.addPage(this.directory);
            return;
        }

        if(recursive) {
            try (Stream<Path> stream = Files.walk(this.directory.toPath())) {
                List<File> fileList = stream.map(Path::toFile).filter(pdfFilter::accept).collect(Collectors.toList());

                if(fileList.isEmpty()) {
                    LOG.info("No existen archivos PDF en el directorio o subdirectorios.");
                    return;
                }

                LOG.info("Añadiendo páginas en blanco en los archivos del directorio de forma recursiva.");

                for(File file : fileList) {
                    this.addPage(file);
                }
            } catch (IOException e) {
                LOG.error("Error de lectura al acceder al directorio.");
                LOG.error("IOException: ", e);
            } catch (SecurityException e) {
                LOG.error("Permisos insuficientes al acceder a un subdirectorio.");
                LOG.error("SecurityException: ", e);
            }
        } else {
            try {
                File[] fileList = this.directory.listFiles(pdfFilter);

                if(fileList.length == 0) {
                    LOG.info("No existen archivos PDF en el directorio.");
                    return;
                }

                LOG.info("Añadiendo páginas en blanco en los archivos del directorio.");

                for(File file : fileList) {
                    this.addPage(file);
                }
            } catch (NullPointerException e) {
                LOG.info("Error de lectura/escritura ocurrido.");
            }
        }
    }

    private void addPage(File filePath) {
        PDPage blankPage = new PDPage();

        try {
            PDDocument document = PDDocument.load(filePath);

            if(document.getEncryption() != null) {
                document.close();
                LOG.error("El documento está protegido contra escritura.");
                return;
            }

            document.addPage(blankPage);

            document.save(filePath);
            document.close();
            LOG.info("Página en blanco añadida al fichero " + filePath.getPath());
        } catch (IOException e){
            LOG.error("Error de lectura/escritura ocurrido.");
            LOG.error("IOException: ", e);
        }
    }

}
