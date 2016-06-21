///*
// * MergeManager.java
// */
//
//package se.liu.ida.sambo.Merger;
//
//import java.net.URL;
//import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//
//import com.hp.hpl.jena.ontology.*;
//
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.RDFWriter;
//import com.hp.hpl.jena.rdf.model.Resource;
//
//import com.hp.hpl.jena.reasoner.ReasonerRegistry;
//import com.hp.hpl.jena.reasoner.dig.*;
//import com.hp.hpl.jena.vocabulary.*;
//
//import se.liu.ida.sambo.MModel.*;
//
//import java.util.Enumeration;
//import java.net.MalformedURLException;
//
//
///**The class manages the ontologies involved in an alignment and merging process
// *
// * ontology 1, ontology 2, and the new ontology.
// *
// * @author  He Tan
// * @version
// */
//public class OntManager {
//
//    OntModel[] OntM =  new OntModel[3];
//    MOntology[] MOnt = new MOntology[3];
//
//
//    /**
//     * Creates new OntManager
//     */
//    public OntManager() {
//
//    }
//
//    /**Loads an ontology.
//     *
//     *@param url the url of the ontology file
//     *
//     *@throws MalformedURLException
//     *
//     *@return the MOntology of the ontology
//     */
//    protected static MOntology loadOntology(String url, boolean inf){
//
//        try{
//            return loadOntology(new URL(url), inf);
//        }catch(MalformedURLException e){
//            throw (RuntimeException) e.fillInStackTrace();
//        }
//    }
//
//
//    /**Loads an OWL ontology.
//     *
//     *@param url the url of the ontology file
//     *
//     *@return the MOntology of the ontology
//     */
//    public static MOntology loadOntology(URL url, boolean inf){
//
//        OntModel ontModel = null;
//        if(inf)
//            ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF, null );
//        else
//            ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
//
//        ontModel.read(url.toString());
//
//        return new MOntology(ontModel);
//
//    }
//
//
//
//    /**Loads one of the original ontologies, ontology-1 or ontology-2
//     *
//     *@param url the url of the ontology file
//     *@param num it indicates the ontology is 1 or 2,
//     *               or a browsing ontology
//     *
//     *@throws MalformedURLException
//     *
//     *@return the MOntology of the ontology
//     */
//    protected void loadOntology(String url, int num){
//
//        try{
//            loadOntology(new URL(url), num);
//        }catch(MalformedURLException e){
//            throw (RuntimeException) e.fillInStackTrace();
//        }
//    }
//
//
//
//
//
//    /**Loads one of the original ontologies, ontology-1 or ontology-2
//     *
//     *@param language the ontology language, like DAML, OWL Lite and so on
//     *@param url the url of the ontology file
//     *@param num it indicates the ontology is 1 or 2,
//     *               or a browsing ontology
//     *
//     *
//     *@return the MOntology of the ontology
//     */
//    protected void loadOntology(URL url, int num){
//
//        //spec.setReasoner( dr );
//        OntM[num] = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
//        //ontModel.read(url.openStream(), Constants.base, Constants.lang);
//        OntM[num].read(url.toString());
//
//        System.out.println("[DEBUG MSG] Loading ontology : " + url.toString() );
//        //create an UI for the ontology
//        MOnt[num] = new MOntology(num, OntM[num]);
//        System.out.println("[DEBUG MSG] Finish loading." );
//    }
//
//
//    /**Builds the the new ontology in the specified language
//     *
//     *@param lan the num indicates the ontology language
//     *
//     *@throws NullPointerException if the language is not support.
//     */
//    public void createAlignOntModel(){
//
//        OntM[Constants.ONTOLOGY_NEW] = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
//        OntM[Constants.ONTOLOGY_NEW].createOntology(Constants.namespace);
//
//        createAlignment();
//    }
//
//
//    /**Builds the the new ontology in the specified language
//     *
//     *@throws NullPointerException if the language is not support.
//     */
//    public static OntModel createOntModel(boolean inf){
//
//      /*  return ((lan == Constants.DAML_OIL) ?
//            ModelFactory.createOntologyModel(OntModelSpec.DAML_MEM, null)
//            : ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null));
//       */
//
//        if(inf)
//            return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
//
//        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
//    }
//
//
//    /**Builds the the new ontology in the specified language
//     *
//     *@throws NullPointerException if the language is not support.
//     */
//    public static OntModel createOntModel(String file, boolean inf){
//
//        OntModel model = null;
//
//        if(inf)
//            model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, null);
//        else  model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
//
//        model.read(file);
//
//        return model;
//    }
//
//    public static DIGReasoner createJenaDIGReasoner(String DIGReasoner){
//
//        // set up a configuration resource to connect to the reasoner
//        Resource conf = ModelFactory.createDefaultModel().createResource();
//        conf.addProperty( ReasonerVocabulary.EXT_REASONER_URL, ModelFactory.createDefaultModel().createResource(DIGReasoner) );
//
//        // create the reasoner factory and the reasoner
//        DIGReasonerFactory drf = (DIGReasonerFactory) ReasonerRegistry.theRegistry().getFactory( DIGReasonerFactory.URI );
//        return  (DIGReasoner) drf.createWithOWLAxioms( conf );
//
//    }
//
//
//    public static OntModel createReasonOntModel(DIGReasoner dr, String file) {
//
//        OntModelSpec spec  = new OntModelSpec( OntModelSpec.OWL_DL_MEM);
//        spec.setReasoner(dr);
//        //file = "file:///" + file;
//        //System.out.println("[DEBUG MSG] file is :" + file);
//        OntModel model =  ModelFactory.createOntologyModel( spec, null );
//        //file = "file:///E:/My Work/project/SAMBO/SAMBO/build/web/ontologies/OWL/behavior_GO.owl";
//        model.read(file);
//
//        return model;
//    }
//
//
//    public static void writeOntology(OntModel ontModel, String file){
//
//        RDFWriter writer = ModelFactory.createDefaultModel().getWriter("RDF/XML-ABBREV");
//        // writer.setErrorHandler(myErrorHandler);
//        writer.setProperty("showXmlDeclaration","true");
//        writer.setProperty("tab","8");
//        writer.setProperty("xmlbase", Constants.base);
//
//
//        try{
//            writer.write(ontModel, new FileOutputStream(file), null);
//        }catch(FileNotFoundException e){
//            throw (RuntimeException) e.fillInStackTrace();
//        }
//    }
//
//
//
//
//
//    /**Writes the alignment and merge ontologies into owl file
//     *
//     *@param file the ontology file
//     */
//    void writeOntology(String alignfile, String mergefile){
//
//        //  RDFWriter writer = ModelFactory.createDefaultModel().getWriter("RDF/XML-ABBREV");
//        RDFWriter writer = OntM[Constants.ONTOLOGY_NEW].getWriter("RDF/XML-ABBREV");
//        // writer.setErrorHandler(myErrorHandler);
//        writer.setProperty("showXmlDeclaration","true");
//        writer.setProperty("tab","8");
//        writer.setProperty("xmlbase", Constants.base);
//
//        //    writer.setProperty("relativeURIs","same-document,relative");
//        //   writer.setProperty("allowBadURIs", "true");
//
//        try{
//            writer.write(OntM[Constants.ONTOLOGY_NEW].getBaseModel(), new FileOutputStream(alignfile), null);
//            writer.write(OntM[Constants.ONTOLOGY_NEW], new FileOutputStream(mergefile), null);
//
//        }catch(FileNotFoundException e){
//            throw (RuntimeException) e.fillInStackTrace();
//        }
//    }
//
//
//
//    /**Gets the MOntology of the specified the ontology
//     *
//     *@param num the num indicating the ontology
//     *
//     *@return the MOntology
//     */
//    public MOntology getMOnt(int num){
//
//        return MOnt[num];
//    }
//
//
//    /**Gets the OntModel of the specified the ontology
//     *
//     *@param num the num indicating the ontology
//     *
//     *@return the OntModel
//     */
//    public OntModel getOntModel(int num){
//
//        return OntM[num];
//    }
//
//
//    /**Create OWL model for Alignments
//     *
//     *return the OntModel
//     */
//    private void createAlignment(){
//
//        OntModel ontModel = OntM[Constants.ONTOLOGY_NEW];
//        ontModel.setNsPrefix("", Constants.base);
//
//        //import the source ontologies
//        OntDocumentManager dm = ontModel.getDocumentManager();
//
//        dm.addModel(((Ontology) OntM[Constants.ONTOLOGY_1].listOntologies().next()).getURI(), OntM[Constants.ONTOLOGY_1]);
//        ontModel.getOntology(Constants.namespace).addImport((Ontology) OntM[Constants.ONTOLOGY_1].listOntologies().next());
//
//        dm.addModel(((Ontology) OntM[Constants.ONTOLOGY_2].listOntologies().next()).getURI(), OntM[Constants.ONTOLOGY_2]);
//        ontModel.getOntology(Constants.namespace).addImport((Ontology) OntM[Constants.ONTOLOGY_2].listOntologies().next());
//
//        dm.loadImports(ontModel);
//
//        System.out.println("Imported models: " + ontModel.listImportedModels().toList().size());
//
//        while(ontModel.listImportedModels().toList().size()<2){
//            //wait for loading the imported models;
//        }
//
//        //handle properties
//        for(Enumeration e1 = MOnt[Constants.ONTOLOGY_1].getProperties().elements(); e1.hasMoreElements();){
//
//            MProperty p = (MProperty) e1.nextElement();
//
//            if(p.getAlignElement() != null){
//
//                if(p.getAlignName() == null){
//
//                    OntProperty pro = ontModel.getOntProperty(p.OntProperty().getURI());
//
//                    pro.addEquivalentProperty(ontModel.getOntProperty(((MProperty)p.getAlignElement()).OntProperty().getURI()));
//                    pro.addComment(p.getAlignComment(), null);
//
//                }else{
//
//                    OntProperty pro = createProperty(p.getType(),  Constants.base + p.getAlignName(), ontModel);
//
//                    pro.addEquivalentProperty(ontModel.getOntProperty(p.OntProperty().getURI()));
//                    pro.addEquivalentProperty(ontModel.getOntProperty(((MProperty)p.getAlignElement()).OntProperty().getURI()));
//
//                    pro.addComment(p.getAlignComment(), null);
//                }
//
//            }
//        }
//
//
//        //handle classes
//        for(Enumeration e1 = MOnt[Constants.ONTOLOGY_1].getClasses().elements(); e1.hasMoreElements();){
//
//            MClass c = (MClass) e1.nextElement();
//           /* System.out.println(c);
//            System.out.println("OntClass : " + c.OntClass());
//            System.out.println("OntClass in new Model : " + ontModel.getOntClass( c.OntClass().getURI()));
//            */
//            OntClass oc;
//
//            if(c.getAlignElement() != null){
//
//                if(c.getAlignName() == null){
//
//                    oc = ontModel.getOntClass(c.OntClass().getURI());
//
//                }else{
//
//                    oc = ontModel.createClass(Constants.base + c.getAlignName());
//                    oc.addEquivalentClass(ontModel.getOntClass( c.OntClass().getURI()));
//                }
//
//             /*   System.out.println("OntClass in new OntModel : " + oc);
//                System.out.println("Alignment MClass : " + (MClass) c.getAlignElement());
//                System.out.println("Alignment OntClass : " +((MClass) c.getAlignElement()).OntClass());
//                System.out.println("Alignment OntClass in new OntModel : " + ontModel.getOntClass(((MClass) c.getAlignElement()).OntClass().getURI()));
//              */
//                oc.addEquivalentClass(ontModel.getOntClass( ((MClass)c.getAlignElement()).OntClass().getURI()));
//                oc.addComment(c.getAlignComment(), null);
//
//            }
//
//            if(!c.getAlignSupers().isEmpty()){
//                oc = ontModel.getOntClass(c.OntClass().getURI());
//
//                for(Enumeration en = c.getAlignSupers().elements(); en.hasMoreElements();)
//                    oc.addSuperClass(ontModel.getOntClass(((MClass) en.nextElement()).OntClass().getURI()));
//
//                oc.addComment(c.getAlignComment(), null);
//            }
//        }
//
//         //handle classes
//        for(Enumeration e2 = MOnt[Constants.ONTOLOGY_2].getClasses().elements(); e2.hasMoreElements();){
//
//            MClass c = (MClass) e2.nextElement();
//
//            if(!c.getAlignSupers().isEmpty()){
//
//                OntClass oc = ontModel.getOntClass(c.OntClass().getURI());
//
//                for(Enumeration en = c.getAlignSupers().elements(); en.hasMoreElements();)
//                    oc.addSuperClass(ontModel.getOntClass(((MClass) en.nextElement()).OntClass().getURI()));
//
//                oc.addComment(c.getAlignComment(), null);
//            }
//        }
//
//    }
//
//
//
//    private static OntProperty createProperty(String type, String uri, OntModel model){
//
//        if(type.equalsIgnoreCase(com.hp.hpl.jena.vocabulary.OWL.ObjectProperty.getLocalName()))
//            return model.createObjectProperty(uri);
//
//        else if(type.equalsIgnoreCase(com.hp.hpl.jena.vocabulary.OWL.DatatypeProperty.getLocalName()))
//            return model.createDatatypeProperty(uri);
//
//        else
//            return model.createOntProperty(uri);
//    }
//
//
//    /**Clear the ontology
//     */
//    void clearOntology(int n){
//
//        OntM[n] = null;
//        MOnt[n] = null;
//    }
//
//
//    /** Clear the alignment information of the loaded ontologies
//     */
//    void resetOntology(int n){
//
//        //clean class alignment information
//        for(Enumeration e = MOnt[n].getClasses().elements(); e.hasMoreElements();){
//
//            MClass mc = (MClass) e.nextElement();
//
//            mc.setAlignElement(null);
//            mc.setAlignName(null);
//
//            while(!mc.getAlignSupers().isEmpty())
//                mc.removeAlignSuper();
//        }
//
//        //clean property alignment information
//        for(Enumeration e = MOnt[n].getProperties().elements(); e.hasMoreElements();){
//
//            MProperty mp = (MProperty) e.nextElement();
//
//            mp.setAlignName(null);
//            mp.setAlignElement(null);
//        }
//
//    }
//
//}
