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
import org.semanticweb.owlapi.model.OWLClassExpression;
import se.liu.ida.sambo.MModel.util.NameProcessor;
import se.liu.ida.sambo.MModel.util.OntConstants;

/** User interface to represent an ontology
 *
 * @author  He Tan
 * @version
 */
public class testMOntology {
    protected OWLOntologyManager manager;
    protected OWLDataFactory factory;
    
    private HashMap<Integer,testMClass> classes;
    private HashMap<String,Integer> classlocalname;
    private HashMap<Integer,testMDataproperty> dataproperties;
    private HashMap<String,Integer> datapropertylocalname;
    private HashMap<String,Integer> objectpropertylocalname;
    private HashMap<Integer,testMObjectproperty> objectproperties;
    private HashMap<Integer,HashSet<testLexicon>> classlexicons;
    private HashMap<Integer,HashSet<testLexicon>> datapropertylexicons;
    private HashMap<Integer,HashSet<testLexicon>> objectpropertylexicons;
    private testURITable urit;
    
    
    private int LOCAL_FILE=0;
    private int NOT_LOCAL_FILE=1;




    public testMOntology(){
        manager=OWLManager.createOWLOntologyManager();
        factory=manager.getOWLDataFactory();
        
        classes=new HashMap<Integer,testMClass>();
        classlocalname = new HashMap<String, Integer>();
        dataproperties=new HashMap<Integer,testMDataproperty>();
        objectproperties=new HashMap<Integer,testMObjectproperty>();
        classlexicons =new HashMap<Integer, HashSet<testLexicon>>();
        datapropertylexicons =new HashMap<Integer, HashSet<testLexicon>>();
        objectpropertylexicons =new HashMap<Integer, HashSet<testLexicon>>();
        urit =new testURITable();
        
    }
    public void loadMOntology(String path) throws OWLOntologyCreationException   {
        File file=new File(path);
        OWLOntology o;
        o=manager.loadOntologyFromOntologyDocument(file);
        
        buildFromOWLOntology(o);
        manager.removeOntology(o);
    }

    /** Creates new MOntology
     *
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
        buildFromOWLOntology(o);
        manager.removeOntology(o);
        
    }
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
        buildFromOWLOntology(o);
        manager.removeOntology(o);
        
    }
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
     * @OWLOntology o: ontology object in OWL API.
     * 
     */
    public void buildFromOWLOntology(OWLOntology o) {
        long t1 = System.currentTimeMillis();
        buildClasses(o);
        
        buildProperties(o);
        System.out.println("Start to build relationships");
        buildRelationships(o);
        System.out.println("End build relationships");
        
        long t2 = System.currentTimeMillis();
        System.out.println( "Time Taken to LOAD FILE " + (t2-t1) + " ms" );
    }
     /**
     * Build classes in ontology.
     * @OWLOntology o: ontology object in OWL API.
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
            HashSet<testLexicon> tlset = new HashSet<testLexicon>();
            for(OWLAnnotation a : annotation)
            {
                testLexicon tl;
                String propertyuri = a.getProperty().getIRI().toString();
                if(getAnnotationtype(propertyuri) != null)
                {
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
                    }
                    // annnotation is IRI
                    else if(a.getValue() instanceof IRI){
                        OWLNamedIndividual nameindividual = factory.getOWLNamedIndividual((IRI) a.getValue());
                        for(OWLAnnotation individual_annotation : nameindividual.getAnnotations(o, rdf_label))
                        {
                            
                        }
                    }
                    else{
                    }
                }
                
            }
            testMClass tmc = new testMClass(class_id,class_uri, class_label);
            classes.put(class_id, tmc);
            classlocalname.put(tmc.getLocalName(), class_id);
            classlexicons.put(class_id, tlset);
            
        }
    }
     /**
     * Build properties in ontology.
     * @OWLOntology o: ontology object in OWL API.
     * 
     */
    public void buildProperties(OWLOntology o){
        buildDataproperties(o);
        buildObjectproperties(o);
        
    }
    /**
     * Build data properties in ontology.
     * @OWLOntology o: ontology object in OWL API.
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
                            
                                }
                            }
                            else{
                                
                            }
                        }
                
                    }
            
                    datapropertylexicons.put(property_id, tlset);
                }

            }
            i++;
        }
        System.out.println(i+" data properties");
    }
     /**
     * Build object properties in ontology.
     * @OWLOntology o: ontology object in OWL API.
     * 
     */
    public void buildObjectproperties(OWLOntology o){
        OWLAnnotationProperty rdf_label = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        Set<OWLObjectProperty> object_properties = o.getObjectPropertiesInSignature(true);
        int i = 0;
        for(OWLObjectProperty oproperty : object_properties)
        {
            String property_uri = oproperty.getIRI().toString();
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
                            
                                }
                            }
                            else{
                            }
                        }
                
                    }
            
                    objectpropertylexicons.put(property_id, tlset);
                }

            }
            i++;
        }
        System.out.println(i+" object properties");
    }
    /**
     * Build relationships sub and super in ontology.
     * @OWLOntology o: ontology object in OWL API.
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
                    testMClass parent_class = getMClass(oc.getIRI().toString());        
                    child_class.addSub(urit.getIndex(child_class.getURI()), parent_class);
                    //System.out.println("class1: "+ parent_class.getclassname()+" class2 uri: "+child_class.getclassname()+" OWL_CLASS.");
                }
                else if(type.equals(ClassExpressionType.OBJECT_ALL_VALUES_FROM)){
                    Set<OWLClass> allvalueclasses = oe.getClassesInSignature();
                    OWLClass oc_all = allvalueclasses.iterator().next();
                    testMClass parent_class = getMClass(oc_all.getIRI().toString());        
                    child_class.addSub(urit.getIndex(child_class.getURI()), parent_class);
                    //System.out.println("class1: "+ parent_class.getclassname()+" class2 uri: "+child_class.getclassname()+" allvaluefrom.");
                }
                else if(type.equals(ClassExpressionType.OBJECT_SOME_VALUES_FROM)){
                    Set<OWLClass> somevalueclasses = oe.getClassesInSignature();
                    OWLClass oc_some = somevalueclasses.iterator().next();
                    testMClass parent_class = getMClass(oc_some.getIRI().toString());        
                    child_class.addSub(urit.getIndex(child_class.getURI()), parent_class);
                    //System.out.println("class1: "+ parent_class.getclassname()+" class2 uri: "+child_class.getclassname()+" somevaluefrom.");
                }
                else{
                }
            }
        }
    }
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
     * @String uri: the uri of the annotation.
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
     * @String uri: the uri of the class.
     * @return true: not owl Thing.
     */
    public boolean checkClassURI(String uri){
        if(uri == null||uri.endsWith("owl#Thing")||uri.endsWith("owl:Thing"))
            return false;
        return true;
    }
    /**
     * Get the MClass object based on the URI of the class.
     * @String uri: the uri of the class.
     * @return MClass or null.
     */
    public testMClass getMClass(String uri){
        int index = urit.getIndex(uri);
        if(classes.containsKey(index))
            return classes.get(index);
        return null;
    }
    /**
     * Get the set class in ontology.
     * @return set of class id in ontology.
     */
    public Set<Integer> getMClasses(){
        return classes.keySet();
    }
    public Set<Integer> getProperties(){
        HashSet<Integer> temp = new HashSet<Integer>();
        temp.addAll(dataproperties.keySet());
        temp.addAll(objectproperties.keySet());
        return temp;
    }
    public Set<Integer> getMDataproperties(){
        return dataproperties.keySet();
    }
    public Set<Integer> getMObjectproperties(){
        return objectproperties.keySet();
    }
    public Set<Integer> getClassLexicons(){
        return classlexicons.keySet();
    }
    public Set<Integer> getPropertiesLexicons(){
        HashSet<Integer> temp = new HashSet<Integer>();
        temp.addAll(datapropertylexicons.keySet());
        temp.addAll(objectpropertylexicons.keySet());
        return temp;
    }
    public Collection<HashSet<testLexicon>> getclasslexiconValues(){
        return classlexicons.values();
    }
    public HashSet<testLexicon> getclasslexicons(int index){
        return classlexicons.get(index);
    }
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
    public testURITable getURITable(){
        return this.urit;
    }
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
    public String getElementURI(String localname){
        if(classlocalname.containsKey(localname)){
            return urit.getURI(classlocalname.get(localname));
        }
        else if(datapropertylocalname.containsValue(localname)){
            return urit.getURI(datapropertylocalname.get(localname));
        }
        else if(objectpropertylocalname.containsValue(localname)){
            return urit.getURI(objectpropertylocalname.get(localname));
        }
        else{
            return null;
        }
    }
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
    public ArrayList roots() {
        ArrayList roots = new ArrayList();
        for(testMClass mclass : classes.values())
        {
            if(mclass.getSuperClasses().isEmpty() && mclass.getSubClasses().isEmpty()){
                roots.add(mclass);
            }
        }
        return roots;
    }
    public HashMap<Integer, testMClass> getClasses(){
        return this.classes;
    }
}
