/*
 * MModel.java
 *
 */
package se.liu.ida.sambo.MModel;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import com.racersystems.racer.ReasonerFactory;
import java.net.URL;
import java.util.Collection;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.HermiT.*;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.NodeID;
import org.semanticweb.owlapi.model.OWLClassExpression;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.MModel.util.OntConstants;

/** User interface to represent an ontology
 *
 * @author  huali50
 * Ontology instance
 */
public class testMOntology {
    //From OWL API-Ontology manager
    protected OWLOntologyManager manager;
    //From OWL API-Data Factory
    protected OWLDataFactory factory;
    //Ontology URI
    protected String uri;
    //Map of ids and class instances
    private HashMap<Integer,testMClass> classes;
    //Map of classlocal and its id
    private HashMap<String,Integer> classlocalname;
    //Map of ids and data properties
    private HashMap<Integer,testMDataproperty> dataproperties;
    //Map of dataproperty local name and id
    private HashMap<String,Integer> datapropertylocalname;
    //Map of objectproperty local name and id
    private HashMap<String,Integer> objectpropertylocalname;
    //Map of class labels and ids
    private HashMap<String,Integer> classlabels;
    //Map of ids and object properties
    private HashMap<Integer,testMObjectproperty> objectproperties;
    //Map of class id and its lexicons
    private HashMap<Integer,HashSet<testLexicon>> classlexicons;
    //Map of data property id and its lexicons
    private HashMap<Integer,HashSet<testLexicon>> datapropertylexicons;
    //Map of object property id and its lexicons
    private HashMap<Integer,HashSet<testLexicon>> objectpropertylexicons;
    //Set of roots
    private HashSet<testMClass> roots;
    //URITable
    private testURITable urit;
    
    private int LOCAL_FILE=0;
    private int NOT_LOCAL_FILE=1;

    public testMOntology(){
        manager=OWLManager.createOWLOntologyManager();
        factory=manager.getOWLDataFactory();
        
        classes=new HashMap<Integer,testMClass>();
        classlocalname = new HashMap<String, Integer>();
        datapropertylocalname = new HashMap<String, Integer>();
        objectpropertylocalname = new HashMap<String, Integer>();
        dataproperties=new HashMap<Integer,testMDataproperty>();
        objectproperties=new HashMap<Integer,testMObjectproperty>();
        classlexicons =new HashMap<Integer, HashSet<testLexicon>>();
        datapropertylexicons =new HashMap<Integer, HashSet<testLexicon>>();
        objectpropertylexicons =new HashMap<Integer, HashSet<testLexicon>>();
        classlabels = new HashMap<String,Integer>();
        roots = new HashSet<testMClass>();
        urit =new testURITable();
        
    }
    /**
     * Load Ontology based on String path
     * @author huali50
     * @param path
     * @throws OWLOntologyCreationException 
     */
    public void loadMOntology(String path) throws OWLOntologyCreationException   {
        long t1 = System.currentTimeMillis();
        File file=new File(path);
        OWLOntology o;
        o=manager.loadOntologyFromOntologyDocument(file);
        this.uri = o.getOntologyID().getOntologyIRI().toString();
        buildFromOWLOntology(o);
        manager.removeOntology(o);
        long t2 = System.currentTimeMillis();
        System.out.println("Time to load ontology is "+ (t2-t1)+ " ms.");
    }
    /**
     * Load Ontology based on URL path
     * @author huali50
     * @param url
     * @throws OWLOntologyCreationException 
     */
    public void loadMOntology(URL url) throws OWLOntologyCreationException {
        OWLOntology o;
        int checkuri=checkURL(url);
        if(checkuri==LOCAL_FILE)
        {
            File file=new File(url.getPath());
            o=manager.loadOntologyFromOntologyDocument(file);
        }
        else
        {
            IRI iri=IRI.create(url.toString());
            o=manager.loadOntology(iri);
        }
        this.uri = o.getOntologyID().getOntologyIRI().toString();
        buildFromOWLOntology(o);
        manager.removeOntology(o);
        
    }
    /**
     * Load Ontology based on URI path
     * @author huali50
     * @param uri
     * @throws OWLOntologyCreationException 
     */
    public void loadMOntology(URI uri) throws OWLOntologyCreationException {
        OWLOntology o;

        if(uri.toString().startsWith("file"))
        {
            File file=new File(uri);
            o=manager.loadOntologyFromOntologyDocument(file);
        }
        else
        {
            IRI iri=IRI.create(uri);
            o=manager.loadOntology(iri);
        }
        this.uri = o.getOntologyID().getOntologyIRI().toString();
        buildFromOWLOntology(o);
        manager.removeOntology(o);
        
    }
    /**
     * Check url starts with "file:" or not
     * @author huali50
     * @param url
     * @return 
     */
    public int checkURL(URL url)
    {
        if(url.toString().startsWith("file:"))
        {
            return LOCAL_FILE;
        }
        else
        {
            return NOT_LOCAL_FILE;
        }
    }
    
    /**
     * Build classes, properties and relationships in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildFromOWLOntology(OWLOntology o) {
        if(o.getOntologyID().getOntologyIRI() != null)
            this.uri = o.getOntologyID().getOntologyIRI().toString();
        long t1 = System.currentTimeMillis();
        buildClasses(o);
        
        buildProperties(o);
        System.out.println("Start to build relationships");
        buildRelationships(o);
        System.out.println("End build relationships");
        
        long t2 = System.currentTimeMillis();
        System.out.println( "Time Taken to LOAD FILE " + (t2-t1) + " ms." );
    }
     /**
     * Build classes in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildClasses(OWLOntology o) {
        OWLAnnotationProperty rdf_label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Set<OWLClass> owlclasses = o.getClassesInSignature(true);
        for(OWLClass c : owlclasses)
        {
            //get class information
            String class_uri = c.getIRI().toString();
            if(checkClassURI(class_uri) == false)
                continue;
            urit.addURI(class_uri);
            int class_id = urit.getIndex(class_uri);
            if(class_id == 0)
                continue;

            //get class annotation
            String class_label = null;
            int label_flag = -1;
            Set<OWLAnnotation> annotation = c.getAnnotations(o);
            for(OWLOntology ont : o.getImports())
		annotation.addAll(c.getAnnotations(ont));
            HashSet<testLexicon> tlset = new HashSet<testLexicon>();
            int anntation_flag = 0;
            for(OWLAnnotation a : annotation)
            {
                testLexicon tl;
                String propertyuri = a.getProperty().getIRI().toString();
                if(getAnnotationtype(propertyuri) != null)
                {
                    anntation_flag = 1;
                    // annotation is String
                    if(a.getValue() instanceof OWLLiteral) {
                        OWLLiteral annotation_value = (OWLLiteral) a.getValue();
                        String name = annotation_value.getLiteral();
                        String language = annotation_value.getLang();
                        tl = new testLexicon("label", language, NameProcessor.getInstance().advCleanName(name));
                        //already get the lexical data. Next step is to store them.
                        tlset.add(tl);
                        if(language.equals(OntConstants.lan)){
                            class_label = name;
                            label_flag = 0;
                        }
                        if(language.equals("en")){
                            if(label_flag == -1)
                                class_label = name;
                        }
                        if(language.isEmpty()){
                            if(label_flag == -1)
                                class_label = name;
                        }
                    }
                    // annnotation is IRI
                    else if(a.getValue() instanceof IRI){
                        OWLNamedIndividual nameindividual = factory.getOWLNamedIndividual((IRI) a.getValue());
                        for(OWLAnnotation individual_annotation : nameindividual.getAnnotations(o, rdf_label))
                        {
                            // to be implemented in the future
                        }
                    }
                    else{
                        continue;
                    }
                }
                
            }
            if(anntation_flag == 1){
                testMClass tmc = new testMClass(class_id,class_uri, class_label);
                if(class_uri.endsWith("owl#Thing")||class_uri.endsWith("owl:Thing"))
                    tmc.setThing();
                classes.put(class_id, tmc);
                classlocalname.put(tmc.getLocalName(), class_id);
                classlabels.put(class_label,class_id);
                classlexicons.put(class_id, tlset);
            }

            
        }
    }
     /**
     * Build properties in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildProperties(OWLOntology o){
        buildDataproperties(o);
        buildObjectproperties(o);
        
    }
    /**
     * Build data properties in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildDataproperties(OWLOntology o){
        OWLAnnotationProperty rdf_label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Set<OWLDataProperty> data_properties = o.getDataPropertiesInSignature(true);
        int i = 0;
        for(OWLDataProperty dproperty : data_properties)
        {
            String property_uri = dproperty.getIRI().toString();
            if(property_uri != null){
                urit.addURI(property_uri);
                int property_id = urit.getIndex(property_uri);
                if(property_id != 0){
                    testMDataproperty tmdp = new testMDataproperty(property_uri);
                    dataproperties.put(property_id, tmdp);
                    
                    Set<OWLAnnotation> annotation = dproperty.getAnnotations(o);
                    HashSet<testLexicon> tlset = new HashSet<testLexicon>();
                    for(OWLAnnotation a : annotation)
                    {
                        testLexicon tl;
                        String annotationpropertyuri = a.getProperty().getIRI().toString();
                        if(getAnnotationtype(annotationpropertyuri) != null)
                        {
                            // annotation is String
                            if(a.getValue() instanceof OWLLiteral) {
                                OWLLiteral annotation_value = (OWLLiteral) a.getValue();
                                String name = annotation_value.getLiteral();
                                String language = annotation_value.getLang();
                                tl = new testLexicon("label", language, name);
                                //already get the lexical data. Next step is to store them.
                                tlset.add(tl);
                        
                            }
                            // annnotation is IRI
                            else if(a.getValue() instanceof IRI){
                                OWLNamedIndividual nameindividual = factory.getOWLNamedIndividual((IRI) a.getValue());
                                for(OWLAnnotation individual_annotation : nameindividual.getAnnotations(o, rdf_label))
                                {
                                    // to be implemented in the future
                                }
                            }
                            else{
                                
                            }
                        }
                
                    }
                   
                    datapropertylocalname.put(tmdp.getName(), property_id);
                    datapropertylexicons.put(property_id, tlset);
                }

            }
            i++;
        }
        //System.out.println(i+" data properties");
    }
     /**
     * Build object properties in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildObjectproperties(OWLOntology o){
        OWLAnnotationProperty rdf_label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Set<OWLObjectProperty> object_properties = o.getObjectPropertiesInSignature(true);
        int i = 0;
        for(OWLObjectProperty oproperty : object_properties)
        {
            String property_uri = oproperty.getIRI().toString();
            if(checkPropertyPrefix(property_uri) == false)
                continue;
            if(property_uri != null){
                urit.addURI(property_uri);
                int property_id = urit.getIndex(property_uri);
                if(property_id != 0){
                    testMObjectproperty tmdp = new testMObjectproperty(property_uri);
                    objectproperties.put(property_id, tmdp);
                    
                    Set<OWLAnnotation> annotation = oproperty.getAnnotations(o);
                    HashSet<testLexicon> tlset = new HashSet<testLexicon>();
                    for(OWLAnnotation a : annotation)
                    {
                        testLexicon tl;
                        String annotationpropertyuri = a.getProperty().getIRI().toString();
                        if(getAnnotationtype(annotationpropertyuri) != null)
                        {
                            // annotation is String
                            if(a.getValue() instanceof OWLLiteral) {
                                OWLLiteral annotation_value = (OWLLiteral) a.getValue();
                                String name = annotation_value.getLiteral();
                                String language = annotation_value.getLang();
                                tl = new testLexicon("label", language, name);
                                //already get the lexical data. Next step is to store them.
                                tlset.add(tl);
                        
                            }
                            // annnotation is IRI
                            else if(a.getValue() instanceof IRI){
                                OWLNamedIndividual nameindividual = factory.getOWLNamedIndividual((IRI) a.getValue());
                                for(OWLAnnotation individual_annotation : nameindividual.getAnnotations(o, rdf_label))
                                {
                                    // to be implemented in the future
                                }
                            }
                            else{
                            }
                        }
                
                    }
                    objectpropertylocalname.put(tmdp.getName(), property_id);
                    objectpropertylexicons.put(property_id, tlset);
                }

            }
            i++;
        }
        //System.out.println(i+" object properties");
    }
    /**
     * Build relationships sub and super in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildRelationships(OWLOntology o) {
        Set<OWLClass> owlclasses = o.getClassesInSignature(true);
        for(OWLClass c : owlclasses)
        {
            
            testMClass child_class = getMClass(c.getIRI().toString()); 
            Set<OWLClassExpression> superclasses = c.getSuperClasses(o);
            for(OWLClassExpression oe : superclasses)
            {
                ClassExpressionType type = oe.getClassExpressionType();
                if(type.equals(ClassExpressionType.OWL_CLASS)){
                    OWLClass oc = oe.asOWLClass();
                    if(oc.getIRI().toString().endsWith("owl#Thing")||oc.getIRI().toString().endsWith("owl:Thing"))
                        continue;
                    testMClass parent_class = getMClass(oc.getIRI().toString());  
                    parent_class.addSub(urit.getIndex(child_class.getURI()), child_class);
                    child_class.addSuper(urit.getIndex(parent_class.getURI()), parent_class);
                    child_class.setRoot(false);
                }
                else if(type.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)){
                    Set<OWLClass> allvalueclasses = oe.getClassesInSignature();
                    OWLClass oc_all = allvalueclasses.iterator().next();
                    if(oc_all.getIRI().toString().endsWith("owl#Thing")||oc_all.getIRI().toString().endsWith("owl:Thing"))
                        continue;
                    testMClass parent_class = getMClass(oc_all.getIRI().toString());   
                    parent_class.addSub(urit.getIndex(child_class.getURI()), child_class);
                    child_class.addSuper(urit.getIndex(parent_class.getURI()), parent_class);
                    child_class.setRoot(false);
                }
                else if(type.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                    Set<OWLClass> somevalueclasses = oe.getClassesInSignature();
                    OWLClass oc_some = somevalueclasses.iterator().next();
                    if(oc_some.getIRI().toString().endsWith("owl#Thing")||oc_some.getIRI().toString().endsWith("owl:Thing"))
                        continue;
                    testMClass parent_class = getMClass(oc_some.getIRI().toString());      
                    parent_class.addSub(urit.getIndex(child_class.getURI()), child_class);
                    child_class.addSuper(urit.getIndex(parent_class.getURI()), parent_class);
                    child_class.setRoot(false);
                }
                else{
                }
            }
        }
    }
        /**
     * Build complex relationships in ontology.
     * @author huali50
     * @param o: ontology object in OWL API.
     * 
     */
    public void buildallrelationships(OWLOntology o, boolean useReasoner){
        OWLReasoner reasoner = null;
        if(useReasoner){
            //Create an ELK reasoner
            OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
            reasoner = reasonerFactory.createReasoner(o);
            Set<OWLClass> classes = o.getClassesInSignature(true);
            for(OWLClass c : classes)
            {
                Set<OWLClass> parents = reasoner.getSuperClasses(c, true).getFlattened();
                for(OWLClass parent : parents)
                {
                    int parentId = urit.getIndex(parent.getIRI().toString());
                    if(parentId > -1){
                    
                    }
                }
            }
        }
        
    }
    /**
     * Get the annotation type based on the URI.
     * @author huali50
     * @param uri: the uri of the annotation.
     * @return LABEL: label type.
     */
    public String getAnnotationtype(String uri) {
        String LABEL="label";
       
        if(uri.endsWith("label"))
            return LABEL;
        return null;
    }
    /**
     * Check the class is Thing or not.
     * @author huali50
     * @param uri: the uri of the class.
     * @return true: not owl Thing.
     */
    public boolean checkClassURI(String uri){
        if(uri == null||uri.endsWith("owl#Thing")||uri.endsWith("owl:Thing"))
            return false;
        return true;
    }
    /**
     * Get the MClass object based on the URI of the class.
     * @author huali50
     * @param uri: the uri of the class.
     * @return MClass or null.
     */
    public testMClass getMClass(String uri){
        int index = urit.getIndex(uri);
        if(classes.containsKey(index))
            return classes.get(index);
        return null;
    }
    /**
     * Get Property
     * @author huali50
     * @param uri
     * @return property
     */
    public testMProperty getProperty(String uri){
        int index = urit.getIndex(uri);
        if(dataproperties.containsKey(index)){
            dataproperties.get(index).setType("DATA");
            return dataproperties.get(index);
        }
        else if(objectproperties.containsKey(index)){
            objectproperties.get(index).setType("OBJECT");
            return objectproperties.get(index);
        }
        else
            return null;
    }
    public testMProperty getPropertyById(int index){
        if(dataproperties.containsKey(index))
            return dataproperties.get(index);
        else if(objectproperties.containsKey(index))
            return objectproperties.get(index);
        else
            return null;
    }
    /**
     * Get the Index Set of all classes in ontology.
     * @author huali50
     * @return set of class id in ontology.
     */
    public Set<Integer> getMClasses(){
        return classes.keySet();
    }
    /**
    * Get the Index Set of all properties in ontology.
    * @author huali50
    * @return set of property id in ontology.
    */
    public Set<Integer> getProperties(){
        HashSet<Integer> temp = new HashSet<Integer>();
        temp.addAll(dataproperties.keySet());
        temp.addAll(objectproperties.keySet());
        return temp;
    }
    /**
    * Get the Index Set of all Data property in ontology.
    * @author huali50
    * @return set of data property id in ontology.
    */
    public Set<Integer> getMDataproperties(){
        return dataproperties.keySet();
    }
    /**
    * Get the Index Set of all object property in ontology.
    * @author huali50
    * @return set of data property id in ontology.
    */
    public Set<Integer> getMObjectproperties(){
        return objectproperties.keySet();
    }
    /**
    * Get the Index Set of all class with lexicons in ontology.
    * @author huali50
    * @return set of class id in ontology.
    */
    public Set<Integer> getClassLexicons(){
        return classlexicons.keySet();
    }
    /**
    * Get the Index Set of all properties with lexicons in ontology.
    * @author huali50
    * @return set of property id in ontology.
    */
    public Set<Integer> getPropertiesLexicons(){
        HashSet<Integer> temp = new HashSet<Integer>();
        temp.addAll(datapropertylexicons.keySet());
        temp.addAll(objectpropertylexicons.keySet());
        return temp;
    }
    /**
    * Get the lexicons of classes in ontology.
    * @author huali50
    * @return lexicons value.
    */
    public Collection<HashSet<testLexicon>> getclasslexiconValues(){
        return classlexicons.values();
    }
    /**
    * Get the class lexicon value in ontology.
    * @author huali50
    * @param index class id
    * @return classlexicon.
    */
    public HashSet<testLexicon> getclasslexicons(int index){
        return classlexicons.get(index);
    }
    /**
    * Get the lexicon value in ontology.
    * @author huali50
    * @param index class or property id
    * @return lexicon.
    */
    public HashSet<testLexicon> getlexicons(int index){
        if(classlexicons.containsKey(index)){
            return classlexicons.get(index);
        }else if(datapropertylexicons.containsKey(index)){
            return datapropertylexicons.get(index);
        }else if(objectpropertylexicons.containsKey(index)){
            return objectpropertylexicons.get(index);
        }else{
            return null;
        }
    }
    /**
    * Get the class lexicon value based on language type in ontology.
    * @author huali50
    * @param index class id
    * @param typr language type
    * @return lexicon.
    */
    public testLexicon getclasslexicon(int index, String type){
        testLexicon classlexicon = null;
        for(testLexicon tl : classlexicons.get(index)){
            if(tl.getlanguage().equals(OntConstants.lan)){
                classlexicon = tl;
                break;
            }
            else if(tl.getlanguage().equals("en")){
                classlexicon = tl;
            }
        }
        return classlexicon;
    }
    /**
    * Get the URITable in ontology.
    * @author huali50
    * @return lexicon.
    */
    public testURITable getURITable(){
        return this.urit;
    }
    /**
    * Get the resource type based on uri.
    * @author huali50
    * @param uri
    * @return resource type.
    */
    public String checkResource(String uri){
        int index = urit.getIndex(uri);
        if(this.classes.containsKey(index)){
            return "class";
        }
        else if(this.dataproperties.containsKey(index)){
            return "dataproperty";
        }
        else if(this.objectproperties.containsKey(index)){
            return "objectproperty";
        }
        else{
            return null;
        }
    }
    /**
    * Get uri based on local name.
    * @author huali50
    * @param localname
    * @return uri.
    */
    public String getElementURI(String localname){
        if(classlocalname.isEmpty() == false && classlocalname.containsKey(localname)){
            return urit.getURI(classlocalname.get(localname));
        }
        else if(datapropertylocalname.isEmpty() == false && datapropertylocalname.containsKey(localname)){
            return urit.getURI(datapropertylocalname.get(localname));
        }
        else if(objectpropertylocalname.isEmpty() == false && objectpropertylocalname.containsKey(localname)){
            return urit.getURI(objectpropertylocalname.get(localname));
        }
        else{
            return null;
        }
    }
    /**
    * Get element based on uri.
    * @author huali50
    * @param uri
    * @return MClass, MDataproperty or MObjectproperty.
    */
    public testMElement getElement(String URI){
        int index = this.urit.getIndex(URI);
        if(classes.containsKey(index))
            return classes.get(index);
        else if(dataproperties.containsKey(index))
            return dataproperties.get(index);
        else if(objectproperties.containsKey(index))
            return objectproperties.get(index);
        else
            return null;
    }
    /**
    * Get root classes.
    * @author huali50
    * @return root classes ArrayList
    */
    public HashSet<testMClass> getRoots() {
        for(testMClass mclass : classes.values())
        {
            if(mclass.getSuperClasses().isEmpty() && (mclass.IsRoot() == true)){
                this.roots.add(mclass);
            }
        }
        return roots;
    }
    /**
    * Get classes set in ontology.
    * @author huali50
    * @return classes set
    */
    public HashMap<Integer, testMClass> getClasses(){
        return this.classes;
    }
    /**
    * Get the property type based on uri.
    * @author huali50
    * @param uri
    * @return "DATAPROPERTY" or "OBJECTPROPERTY" or null
    */
    public String getPropertyType(String URI){
        int index = urit.getIndex(URI);
        if(dataproperties.containsKey(URI))
            return "DATAPROPERTY";
        else if(objectproperties.containsKey(URI))
            return "OBJECTPROPERTY";
        else
            return null;
    }
    /**
     * Get URI of Ontology
     * @author huali50
     * @return 
     */
    public String getOntologyURI(){
        return this.uri;
    }
    /**
     * Get class uri based on label
     * @authro huali50
     * @param label
     * @return URI or null
     */
    public String getClassURI(String label){
        if(this.classlabels.get(label) != null)
            return this.urit.getURI(this.classlabels.get(label));
        else
            return null;
    }
    public boolean checkPropertyPrefix(String propertyuri){
        if(this.getOntologyURI().equals(propertyuri.substring(0, propertyuri.indexOf("#"))))
            return true;
        else
            return false;
    }
}
