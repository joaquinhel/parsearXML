package tarea_8;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


//Hereda de a Jframe implementa la clase abstracta ActionListener
public class AgendaProg8 extends JFrame implements ActionListener {

    File archivoXML;  //Proporciona informaci�n acerca de los archivos, de sus atributos, de los directorios...
    Document documentoDOM;   //Este objeto es la interfaz que contendra el documento XML al completo.
    Transformer transformer; //Transforma el 

    ArrayList<String> datosPersonales; //Array de tama�o variable. Implementa la interfaz List.
    TreeSet<String> telefonos; //Conjunto que permite la ordenaci�n de los valores que contiene, no permite duplicados.
    TreeSet<String> emails;  //Conjunto que permite la ordenaci�n de los valores que contiene, no permite duplicados.

    JPanel panel; //Panel contenedor que tendr�
    JButton botonParse, botonCerrar; // El bot�n que realizar� la transformaci�n
    JTextArea textAreaSuperior, textAreaInferior; //Las dos cajas de texto

//M�todo constructor de la clase
    public AgendaProg8(String titulo) {
        setTitle(titulo);
        iniciarLookAndFeel();
        iniciarContenedores();
        iniciarControles();
        a�adirListeners();
        setVisible(true);
    }

    /*M�todo para cambiar la apariencia de la aplicaci�n y obtenido de:
     https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/nimbus.html*/
    private void iniciarLookAndFeel() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Se ha producido un error");
        }

    }

    private void iniciarContenedores() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Elimina la aplicaci�n de memoria y de CPU
        setResizable(false);
        setSize(620, 480);
        panel = new JPanel();
        setContentPane(panel);// Establecemos panelPrincipal como panel por defecto.  
        panel.setLayout(null);
    }

// M�todo que a�ade los distintos componenetes a la interfaz d�ndole el formato adecuado.
    private void iniciarControles() {
        // Creamos el �rea de texto superior
        textAreaSuperior = new JTextArea(); //Creamos un objeto textArea
        textAreaSuperior.setSize(600, 45); //Definimos el tama�o que tendr�
        textAreaSuperior.setLocation(5, 5); //Definimos la posici�n de la esquina izquierda respecto al componente padre(Jpanel)
        textAreaSuperior.setLineWrap(true);//Crea una nueva l�nea si el texto no cabe en la horizontal
        textAreaSuperior.setWrapStyleWord(true); //Evita que se corten las palabras, sino cabe se envia a la linea siguiente
        panel.add(textAreaSuperior); //A�adimos el panel que hemos creado

        // Creamos el boton que har� la transformaci�n
        botonParse = new JButton("Convertir en XML");
        botonParse.setSize(150, 40);//tama�o del bot�n
        botonParse.setLocation(100, 50);
        panel.add(botonParse);

        //Creamos el boton cerrar
        botonCerrar = new JButton("Cerrar Aplicacion");
        botonCerrar.setSize(150, 40);
        botonCerrar.setLocation(350, 50);
        panel.add(botonCerrar);

        // Creamos el �rea de texto inferior
        textAreaInferior = new JTextArea();
        textAreaInferior.setSize(600, 360);
        textAreaInferior.setLocation(5, 90);
        textAreaInferior.setLineWrap(true);
        textAreaInferior.setWrapStyleWord(true);
        panel.add(textAreaInferior);
    }

    /* M�todo privado que a�ade los listeners (pone a la escucha) a cada uno de los componentes 
     fuente o de origen de eventos.
     Es decir ponemos como oyentes a distintos componentes de le interfaz creada.
     Como par�metro pasamos, en este caso this, es la propia clase Jframe la que hace de oyente y esta
     implementa la interfaz actionListener, que viene definido por el m�todo actionPerformed*/
    private void a�adirListeners() {
        botonParse.addActionListener(this);
        botonCerrar.addActionListener(this);
    }

    /* M�todo que implementa la l�gica de la aplicaci�n mediante la gesti�n de los distintos tipos
     * de eventos que se pueden generar en la aplicaci�n. Usamos else if para ahorrar tiempo de computaci�n.*/
    //Cuando ocurre un actionEvent(hacer clic) llega a est� m�todo y se implementa la accion adecuada. 
    //Este caso se entender�a como: Est� ocurriendo la acci�n e.getSource(), es decir el flujo del programa 
    //est� en un determinado elemento(item o combo) y dependiendo de su nombre llamar�mos al m�todo que nos interese
    @Override //Reescribimos los m�todos de la interfaz ActionListener-en este caso actionPerformed
    //actionPerformed--> M�todo que marca que hacer cuando se recibe un evento por par�mentro
    public void actionPerformed(ActionEvent e) {
        if ((e.getSource() == botonParse)) { //Cuando hacemos clic en itemNuevo
            divideString2XML(); //Llamamos al m�todo DivideString2XML()
        } else if ((e.getSource() == botonCerrar)) {
            System.exit(0); //Cerramos la aplicaci�n
        }
    }

    /*M�todo que utilizamos para dividir la cadena String que se introduce en el textArea superior de la 
     aplicaci�n en las distintas partes que lo componen (DNI, nombre, tel�fono, tel�fonos y emaials). 
     A cada una de las partes le damos el formato definido en el enunciado y por �ltimo llamamos al
     m�todo que visualizar� todos los datos en el textArea inferior */
    private void divideString2XML() {
        try {
            // Dividir el String en partes cada vez que haya una ","
            String texto = textAreaSuperior.getText(); //Recibimos el texto del �rea de texto en un String
            String[] partes = texto.split(","); // Array en el que guardamos el String dividido

            datosPersonales = new ArrayList(); //El array de tama�o variable almacena el DNI, nombre y apellido
            telefonos = new TreeSet<>(Collections.reverseOrder()); //Orden inverso al natural, implementa la interfaz comparable
            emails = new TreeSet<>(); // TreeSet guarda los datos ordenados y no crea duplicados.

            // Quitamos las comillas y los espacios de los campos del nombre, apellidos y DNI
            partes[0] = partes[0].replaceAll("\"", "").trim();
            partes[1] = partes[1].replaceAll("\"", "").trim();// "\"" --> Para eliminar las comillas Y los espacios
            partes[2] = partes[2].replaceAll("\"", "").trim();

            // Agregamos los datos al ArrayList de datos
            datosPersonales.add(partes[0]); // a�adimos el DNI/NIE
            datosPersonales.add(partes[1]); // a�adimos el nombre
            datosPersonales.add(partes[2]); // a�adimos el apellido

            // Recorremos y moficamos los elementos del array a partir de la posici�n 3
            for (int i = 3; i < partes.length; i++) {
                if (partes[i].contains("@")) {// Si contiene @ es un email
                    partes[i] = partes[i].toLowerCase();//Convertimos el texto en minusculas
                    partes[i] = partes[i].trim(); //Quitamos los posibles espacios de una cadena
                    emails.add(partes[i]); //A�adimos al TreeSet cada uno de los emails
                } else { // sino es email es n�mero de tel�fono
                    partes[i] = partes[i].replaceAll("\"", ""); //Eliminamos las comillas
                    partes[i] = partes[i].replaceAll("[(]", "");//Eliminamos los par�ntesis
                    partes[i] = partes[i].replaceAll("[)]", "");
                    telefonos.add(partes[i]);//A�adimos al TreeSet cada uno de los telefonos
                }
            }
            // Antes de realizar la visualizaci�n borramos los datos existentes en el textArea
            textAreaInferior.setText("");
            // Visualizamos los ArrayList y TreeSet pasandole distintos par�metros al m�todo verDatosAlmacenados
            verDatosAlmacenados(datosPersonales);
            verDatosAlmacenados(telefonos);
            verDatosAlmacenados(emails);
            crearDOM();
            crearXML();
            visualizarXML();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Faltan datos. Debe introducir DNI, nombre, apellidos, email y tel�fono");
        }
    }
    /*M�todo para visualizar en el textArea inferior cada una de las estructuras de almacenamiento.
     Puesto que tanto los Treeset como los Array list heredan de la interfaz collection podemos usarla 
     para ambos tipos de datos (Collection es como su pusieramos otro tipo de dato Integer, Array...)
     <String> no es obligatorio pero facilita el funcionamiento del m�todo*/

    private void verDatosAlmacenados(Collection<String> lista) {
        //Al ser tres estructuras de datos, cada vez que se itera una debemos recoger los datos y guardalos en 
        //una variable. A estos datos le sumaremos los de la siguiente estructura.
        String texto;
        texto = textAreaInferior.getText();
        Iterator<String> it = lista.iterator();
        while (it.hasNext()) { //Mientras queden datos en la lista
            texto = texto + it.next() + "\n"; //En cada iteraci�n acumulamos un dato m�s en variable texto
        }
        textAreaInferior.setText(texto); //Mostramos en el �rea inferior todos los datos
    }

    /*M�todo con el que vamos creando la estructura del documento DOM, vamos a�adiendo los nodos,comentarios, textos...
     Mediante las expresiones regulares nos aseguramos que el formato de los datos es el correcto */
    private void crearDOM() {
        // En directorioActual almaceno el directorio de trabajo actual. "user.dir" esta establecido en el sistema.
        String directorioActual = System.getProperty("user.dir");
        // Creamos una instancia del selector de objetos y le pasamos el "path" del directorio como par�metro
        JFileChooser ventanaChooser = new JFileChooser(directorioActual);
        //Definimos las extensi�n que podremos utilizar en la aplicaci�n
        FileNameExtensionFilter extensionesAdmitidas = new FileNameExtensionFilter("Texto plano (*.xml)", "xml", " ");
        ventanaChooser.setFileFilter(extensionesAdmitidas); //A�adimos las extensiones al JFileChooser
        int seleccion = ventanaChooser.showSaveDialog(this); //Guardamos la seleccion en un int

        if (seleccion == JFileChooser.APPROVE_OPTION) { //Si la opcion que ha escogido el usuario es Guardar Fichero
            archivoXML = ventanaChooser.getSelectedFile();//Guardamos en el archivo creado por el usuario el archivoXML
            try {
                //Generamos una interfaz que producir DOM a partir de XML.
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                //Utilizando la interfaz anterior generamos el DOM a partir del xml
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                //Creamos un nuevo DOM para construir el arbol de la aplicacion
                documentoDOM = docBuilder.newDocument();

                // Creamos el elemento raiz 
                Element raiz = documentoDOM.createElement("datos_cliente"); //Damos nombre a la etiqueta
                documentoDOM.appendChild(raiz); // A�adimos el nodo que hemos creado al final del documento

                // Creamos el elemento id (DNI/NIE) 
                Element id = documentoDOM.createElement("id"); //Damos nombre a la etiqueta
                id.appendChild(documentoDOM.createTextNode(datosPersonales.get(0)));//A�adimos un nodo de texto que contendr� el nombre, posici�n 0 del ArrayList
                raiz.appendChild(id);// A�adimos el nodo al final del documento
                // Compruebo si el DNI/NIE es correcto y si no es as� a�ado un comentario
                if (!compararPattern("([XY]?)([0-9]{1,9})([A-Za-z])", datosPersonales.get(0))) {
                    Comment comentario = documentoDOM.createComment("DNI incorrecto");
                    id.appendChild(comentario);
                }

                // Creamos el elemento nombre 
                Element nombre = documentoDOM.createElement("nombre"); //Damos nombre a la etiqueta
                nombre.appendChild(documentoDOM.createTextNode(datosPersonales.get(1))); //A�ado ArrayList(1)
                raiz.appendChild(nombre); //A�adimos el nodo al final del documento
                // Comprobamos que son todo letras
                if (!compararPattern("[A-Za-z]+", datosPersonales.get(1))) { // Si la comparaci�n vale FALSE
                    Comment comentario = documentoDOM.createComment("nombre incorrecto"); //Devolvemos el comentario
                    nombre.appendChild(comentario); //A�adimos el nodo al final del documento
                }

                // Creamos el elemento apellido
                Element apellidos = documentoDOM.createElement("apellidos"); //Damos nombre a la etiqueta
                apellidos.appendChild(documentoDOM.createTextNode(datosPersonales.get(2))); //A�ado ArrayList(2)
                raiz.appendChild(apellidos);//A�ado el nodo al final del documento
                // Comprobamos que son todo letras
                if (!compararPattern("[A-Za-z]+", datosPersonales.get(2))) { // Si la comparaci�n vale FALSE
                    Comment comentario = documentoDOM.createComment("apellido incorrecto"); //Creamos el comentario
                    apellidos.appendChild(comentario); //A�adimos el nodo al final del documento
                }

                // Creamos el elemento tel�fono
                Element telefonosItem = documentoDOM.createElement("telefonos"); //Damos nombre a la etiqueta que contendr� todos los telf
                raiz.appendChild(telefonosItem); //A�adimos el nodo
                // Atributo que contendr� el total de telefonos
                Attr totalTelefonosAtr = documentoDOM.createAttribute("total");
                totalTelefonosAtr.setValue(Integer.toString(telefonos.size())); //Numeros de telefonos totales
                telefonosItem.setAttributeNode(totalTelefonosAtr); //A�adimos el atributo al nodo
                // Creamos los elemento tel�fonos
                String varApoyo;
                Iterator<String> it = telefonos.iterator(); //Iteramos el TreeSet Telefonos(est�n almacenados en orden inverso)
                while (it.hasNext()) { //Mientra haya telefonos
                    varApoyo = it.next(); //Avanzamos una posici�n en el TreeSet
                    Element telefono = documentoDOM.createElement("telefono"); //Creamos el nodo telefono <etiqueta>
                    telefono.appendChild(documentoDOM.createTextNode(varApoyo)); //Creamos el nodo con el num de telefono
                    telefonosItem.appendChild(telefono); //A�adimos el atributo al nodo
                    if (!compararPattern("\\+?[0-9]{9}[0-9]*", varApoyo)) { // Si la comparaci�n vale FALSE
                        Comment comentario = documentoDOM.createComment("numero incorrecto"); //Creamos el comentario
                        telefono.appendChild(comentario); //A�adimos el nodo al final del documento
                    }
                }

                // Creamos el elemento emails
                Element mailsItem = documentoDOM.createElement("mails"); //Damos nombre a la etiqueta que contendr� todos los emails
                raiz.appendChild(mailsItem); //La a�adimos al final del DOM
                ////Iteramos el TreeSet que contiene los emails 
                Iterator<String> it2 = emails.iterator();
                while (it2.hasNext()) { //Mientras queden tel�fonos en la lista
                    varApoyo = it2.next(); //Guardo el email en la variable de apoyo
                    Element mail = documentoDOM.createElement("mail"); //Doy nombre a la etiqueta
                    mail.appendChild(documentoDOM.createTextNode(varApoyo));//Creo un textNode con el valor del email=varApoyo
                    mailsItem.appendChild(mail); //A�ado el imail al DOM
                    if (!compararPattern("^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,3})$", varApoyo)) {
                        Comment comentario = documentoDOM.createComment("Email incorrecto");
                        mail.appendChild(comentario);
                    }
                }
            } catch (ParserConfigurationException pce) {
                JOptionPane.showMessageDialog(this, "Se ha producido un error");
            } catch (ArrayIndexOutOfBoundsException ae) {
                JOptionPane.showMessageDialog(this, "Se ha producido un error");
            }
        }
    }

    /*M�todo cuya funci�n es la de crear el documento XML a partir del DOM*/
    private void crearXML() {
        try {
            // Creamos el DOMSource, intermediaria entre el transformador y el �rbol DOM.
            DOMSource source = new DOMSource(documentoDOM);
            // Creamos el StreamResult, intermediria entre el transformador y el archivo de destino.
            StreamResult result = new StreamResult(archivoXML);
            // Creamos una nueva instancia del transformador a trav�s de la f�brica de transformadores.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            //Transforma el c�digo fuente en resultados legibles
            transformer = transformerFactory.newTransformer();
            // Realizamos la transformaci�n.
            transformer.transform(source, result);
            // Aviso de la creaci�n del fichero
            JOptionPane.showMessageDialog(this, "El fichero ha sido grabado en:" + archivoXML);
        } catch (TransformerException tfe) {
            JOptionPane.showMessageDialog(this, "Se ha producido un error");
        }
    }

    /*M�todo cuya funcionalidad es la de transformar los datos que tenemos en el DOM a
     una cadena de String y finalmente devolverlo a la aplicaci�n*/
    private void visualizarXML() {
        try {
            DOMSource source = new DOMSource(documentoDOM); //Fuente para el m�todo de transformaci�n
            StringWriter outWriter = new StringWriter(); //Almacena los caracteres en un buffer para construir un String
            // Creamos el StreamResult, intermediria entre el transformador y el archivo de destino.
            StreamResult resultado = new StreamResult(outWriter); //Soporte para la salida del metodo de transformaci�n
            // Realizamos la transformaci�n, introduciendo el c�digo fuente y obtenido 
            transformer.transform(source, resultado);
            StringBuffer sb = outWriter.getBuffer();
            String finalString = sb.toString(); //Pasamos el buffer a String
            textAreaInferior.setText(textAreaInferior.getText() + finalString);
        } catch (TransformerException tfe) {
            JOptionPane.showMessageDialog(this, "Se ha producido un error");
        }
    }

    /*M�todo utilizado para comprobar si la cadena de datos introducida cumple con los requisitos
     establecidmos en el patr�n.
     Debemos introducir como par�metros el patr�n y el dato que queremos analizar. Si es correcto
     devolver� TRUE en caso contrario FALSE*/
    private boolean compararPattern(String patron, String dato) {
        Pattern p = Pattern.compile(patron); //Compilamos la expresi�n regular
        Matcher m = p.matcher(dato); //Comprobamos si la cadena cumple el patr�n
        if (m.matches()) { //Comprobamos si la cadena al completo cumple el patr�n, si puede habe caracteres adicionales uso m.lookingAt()
            return true;
        } else {
            return false;
        }
    }
}
