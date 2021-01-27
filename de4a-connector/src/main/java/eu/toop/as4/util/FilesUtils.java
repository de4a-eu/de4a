package eu.toop.as4.util; 

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
 
 
public class FilesUtils implements Serializable{
	private static Log log = LogFactory.getLog(FilesUtils.class);
	/** Constante serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The file separator. */
	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final int BUFFER_SIZE = 1024;
    
    /**
     * Creates the temp directory.
     *
     * @return the file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static File createTempDirectory()   throws IOException  {
        final File temp;
        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if(!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        return (temp);
    }
    
    /**
     * Empaquetar zip.
     *
     * @param tempDir the temp dir
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] empaquetarZip(  File tempDir) throws  Exception{
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	try {
    		ZipOutputStream zos = new ZipOutputStream(baos);
    		byte bytes[] = new byte[2048];
    	    for (String fileName : tempDir.list()) {
	    	    FileInputStream fis = new FileInputStream(tempDir.getPath() + FILE_SEPARATOR + fileName);
	    	    BufferedInputStream bis = new BufferedInputStream(fis);
	    	    zos.putNextEntry(new ZipEntry(fileName));
	    	    int bytesRead;
	    	    while ((bytesRead = bis.read(bytes)) != -1) {
	    	    	zos.write(bytes, 0, bytesRead);
	    	    }
	    	    zos.closeEntry();
	    	    bis.close();
	    	    fis.close();
    	    }
    	    zos.flush();
	   		baos.flush();
	   		zos.close();
	   		baos.close();
	   	} catch ( Exception e) {
    		String msg = "Se produjo un error empaquetando los justificantes de transmision: "+e.getMessage();
    		throw new  Exception(msg );
    	}
    	return baos.toByteArray();
    }

    /**
     * Metodo que elimina caracteres reservados que no puede ser establecidos como parte del nombre de un fichero.
     * Estos son: \ / : * ? " < > |
     *
     * @param nameFile the name file
     * @return String nombre del fichero sin los caracteres reservados.
     */
    public  static String removeIlegalCharacters(String nameFile){
		if(nameFile ==null)return nameFile;
		nameFile =nameFile.replace("\\", "");
		nameFile =nameFile.replace("/", "");
		nameFile =nameFile.replace(":", "");
		nameFile =nameFile.replace("*", "");
		nameFile =nameFile.replace("?", "");
		nameFile =nameFile.replace("\"", "");
		nameFile =nameFile.replace("<", "");
		nameFile =nameFile.replace(">", "");
		nameFile =nameFile.replace("|", "");
		return nameFile;
	}
    /**
     * Metodo que retorna, dado una ruta absoluta de fichero,
     * el nombre del mismo
     * @return String name del fichero
     * */
    public static String getNameFile(String path){
    	String separator=System.getProperty("file.separator");
    	int pos=path.lastIndexOf(separator);
    	pos=pos==-1?0:pos+1;
    	return path.substring(pos);
    }
    
    /**
	 *Metodo que lee las n ultimas lineas de un fichero y las escribe en otro
	 * @throws IOException 
	 */
	
	public static String tail( File file, int lines) {
	    java.io.RandomAccessFile fileHandler = null;
	    try {
	        fileHandler = 
	            new java.io.RandomAccessFile( file, "r" );
	        long fileLength = fileHandler.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        int line = 0;

	        for(long filePointer = fileLength; filePointer != -1; filePointer--){
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();

	            if( readByte == 0xA ) {
	                line = line + 1;
	                if (line == lines) {
	                    if (filePointer == fileLength) {
	                        continue;
	                    }
	                    break;
	                }
	            } else if( readByte == 0xD ) {
	                line = line + 1;
	                if (line == lines) {
	                    if (filePointer == fileLength - 1) {
	                        continue;
	                    }
	                    break;
	                }
	            }
	            sb.append( ( char ) readByte );
	        }

	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch( java.io.FileNotFoundException e ) {
	        log.error("Error abriendo el fichero: "+e.getMessage());
	        return null;
	    } catch( java.io.IOException e ) {
	    	  log.error(e.getMessage());
	        return null;
	    }
	    finally {
	        if (fileHandler != null )
	            try {
	                fileHandler.close();
	            } catch (IOException e) {
	            }
	    }
	}
	 public void unzip(String zipFilePath, String destDirectory) throws IOException {
	        File destDir = new File(destDirectory);
	        if (!destDir.exists()) {
	            destDir.mkdir();
	        }
	        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
	        ZipEntry entry = zipIn.getNextEntry();
	        // iterates over entries in the zip file
	        while (entry != null) {
	            String filePath = destDirectory + File.separator + entry.getName();
	            if (!entry.isDirectory()) {
	                // if the entry is a file, extracts it
	                extractFile(zipIn, filePath);
	            } else {
	                // if the entry is a directory, make the directory
	                File dir = new File(filePath);
	                dir.mkdirs();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        zipIn.close();
	    }
	    /**
	     * Extracts a zip entry (file entry)
	     * @param zipIn
	     * @param filePath
	     * @throws IOException
	     */
	    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
	        byte[] bytesIn = new byte[BUFFER_SIZE];
	        int read = 0;
	        while ((read = zipIn.read(bytesIn)) != -1) {
	            bos.write(bytesIn, 0, read);
	        }
	        bos.close();
	    }
}
