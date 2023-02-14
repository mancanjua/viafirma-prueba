Autor: Manuel Cañizares Juan

La actual implementación que resuelve el problema presentado, consiste en la clase `PDFTools`, la cual presenta dos constructores, uno sin parámetros para obtener el directorio de la variable de entorno `VIAFIRMA_PATH` y otro que admite una cadena de caracteres para especificar un directorio distinto.

Para llevar a cabo la acción de añadir una página en blanco al final de cualquier documento, se ejecuta el método `addPagesDirectory`. Este método se encarga de, dado el previo directorio, realizar la búsqueda de archivos que tengan formato PDF. El método acepta un parámetro booleano sobre si se desea realizar una búsqueda recursiva por todos los posibles directorios dentro del directorio especificado o mantenerse únicamente dentro de los archivos del directorio inicialmente especificado.

En caso de que el directorio proporcionado se trate de un archivo, si es un archivo PDF, hace una llamada al método que añade la página en blanco.

Una vez obtenidas la lista de archivos PDF, por cada uno de ellos se realiza una llamada al método `addPage`, el cual dada la ruta de un archivo PDF, añade una página en blanco al final del mismo.


### Cómo harías para poder ejecutar N veces el proceso sobre el mismo directorio y solo modificar cada PDF una sola vez?

Para evitar modificar PDFs múltiples veces, se me ocurren dos opciones distintas, una haciendo uso de un registro de documentos ya modificados, y, otra, implementando un método que permita, en este caso, comparar la última página del documento con la página en blanco que se pretende insertar.

Para llevar a cabo el registro de documentos modificados, sería necesario establecer algún código de identificación de cada documento, bien sea el nombre o cualquier otra combinación de propiedades del documento en cuestión y registrarla en una base de datos, un documento, etc.

De la otra forma, habría que implementar una forma alternativa de comparación a la actual. La clase `PDPage` tiene un método `equals` implementado el cual verifica que la página con la que compara la original es de la clase `PDPage` y, verifica que el atributo `page` de la página original es exactamente el mismo que el de la página con la que es comparada.

Este atributo `page` es una instancia de la clase `COSDictionary`, la cual contiene toda la información relativa a los objetos que una página contiene en su interior. Es por ello que, para realizar una correcta comparación entre dos páginas, debe asegurarse que todos los elementos que componen dicha página sean copias exactas.

### Qué pasa si el directorio contiene un fichero que no es un PDF?

De la forma en la que se ha explicado previamente, esto no supone ningún problema, ya que dado un directorio, se recorren todas las rutas que contiene dicho directorio, comprobando en primer lugar si se trata de un archivo y no de otro directorio, y, después, comprobando si el archivo en cuestión se trata de un fichero PDF.

### Cómo probar/ejecutar la aplicación?

En primer lugar, una vez clonado el repositorio se deben descargar las librerías necesarias mediante maven.

Luego, para ejecutar el código, en primer lugar se ha de instanciar la clase `PDFTools`, sin parámetros para utilizar la variable de entorno o especificando una ruta para utilizar un directorio diferente. Posteriormente, se debe ejecutar el método `addPagesDirectory` especificando si la búsqueda de archivos debe o no ser recursiva.

Un ejemplo de ejecución puede ser el siguiente:
```java
PDFTools tools = new PDFTools("D:\Programación\viafirma\viafirma-prueba");

tools.addPageDirectory(false);
```

La ejecución resulta en la siguiente salida por Consola:
```
[INFO ] 2023-02-13 17:12:08.356 [main] PDFTools - Directorio establecido: D:\Programación\viafirma\viafirma-prueba
[INFO ] 2023-02-13 17:12:08.362 [main] PDFTools - Añadiendo páginas en blanco en los archivos del directorio.
[INFO ] 2023-02-13 17:12:08.571 [main] PDFTools - Página en blanco añadida al fichero D:\Programación\viafirma\viafirma-prueba\test.pdf
```

Otra forma de ejecutar el código es mediante consola. Primero se debe compilar usando `mvn compile` y empaquetar usando `mvn package`. Esto creará el archivo `viafirma-prueba-1.0-SNAPSHOT.jar` en el directorio `/target/`.

Una vez hecho esto, podemos ejecutar el programa con:
```
java -jar ./viafirma-prueba-1.0-SNAPSHOT.jar
```

Añadiendo argumentos detrás del archivo se pueden modificar tanto el modo recursivo como el directorio en el que buscar estos archivos.

Estableciendo un único argumento, primero se comprobará si el valor es `true` o `false`, en cuyo caso se interpretará como el modo recursivo. Si el valor es cualquier otra cadena, se interpretará como un directorio.

Estableciendo dos argumentos o más, se interpretará el primero como el directorio donde se desea buscar y el segundo como el modo recursivo, siempre y cuando el valor sea `true` o `false`.

Otra forma de establecer el directorio o el modo recursivo es con las propiedades `-Ddirectory=` y `-Drecursive=` respectivamente. El valor de las propiedades sobrescribirá a los argumentos en caso de que ambos sean usados simultáneamente.

Ejemplos:
```
java -jar ./viafirma-prueba-1.0-SNAPSHOT.jar "D:\Programación\viafirma\viafirma-prueba" false
```
```
java -Ddirectory="D:\Programación\viafirma\viafirma-prueba" -Drecursive=false -jar ./viafirma-prueba-1.0-SNAPSHOT.jar
```

Ambas ejecuciones tendrán el mismo resultado previamente mostrado:

```
[INFO ] 2023-02-13 17:12:08.356 [main] PDFTools - Directorio establecido: D:\Programación\viafirma\viafirma-prueba
[INFO ] 2023-02-13 17:12:08.362 [main] PDFTools - Añadiendo páginas en blanco en los archivos del directorio.
[INFO ] 2023-02-13 17:12:08.571 [main] PDFTools - Página en blanco añadida al fichero D:\Programación\viafirma\viafirma-prueba\test.pdf
```

### Cómo podemos ver los logs?

Para llevar a cabo un registro de todas las acciones, se utiliza la librería Log4j, mediante la cual se han establecido, en distintos puntos del código, diferentes tipos de trazas, en este caso informativas y sobre errores en el funcionamiento de la aplicación.

Se ha establecido una configuración de dicha librería para que dichas trazas se muestren tanto por consola como en un archivo en `./logs/pdtoolslog.log`. Cada traza muestra el tipo, la fecha y hora, el hilo en el que se produce, la clase donde se produce y un mensaje. Para un formato distinto de las mismas, modificar los lugares donde se reflejan dichas trazas o incluso añadir nuevas, se ha de modificar el archivo `/src/main/resources/log4j2.properties`.
