/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package se.liu.ida.sambo.util;

/**This enum is used for easily retrieve the locations of ontologies
 *
 * @author Qiang Liu
 * Created on Jul 22, 2010, 7:08:35 PM
 */
public enum enumOnto {
    Test("test", "file:///Z:/Dataset/OntoNetwork/test.owl"),
    Test1("test1", "file:///Z:/Dataset/OntoNetwork/test1.owl"),
    Test2("test2", "file:///Z:/Dataset/OntoNetwork/test2.owl"),

    Benchmark_101("101", "http://co4.inrialpes.fr/align/Contest/101/onto.rdf"),
    Benchmark_301("301", "http://co4.inrialpes.fr/align/Contest/301/onto.rdf"),
    Benchmark_302("302", "file:///Z:/Dataset/other/Benchmark_Network/302/onto302.rdf"),
    Benchmark_303("303", "file:///Z:/Dataset/other/Benchmark_Network/303/onto303.rdf"),
    Benchmark_304("304", "http://co4.inrialpes.fr/align/Contest/304/onto.rdf"),

    MyOnto1("MyOnto1", "file:///Z:/DataSet/myonto/onto1.owl"),
    MyOnto2("MyOnto2", "file:///Z:/DataSet/myonto/onto2.owl"),

    Anatomy_MA_2009("Ant1", "file:///Z:/DataSet/anatomy/mouse_anatomy_2009.owl"),
    Anatomy_MA_2010("Ant1", "file:///Z:/DataSet/anatomy/mouse_anatomy_2010.owl"),
    Anatomy_NCI_2009("Ant2", "file:///Z:/DataSet/anatomy/nci_anatomy_2009.owl"),
    Anatomy_NCI_2010("Ant2", "file:///Z:/DataSet/anatomy/nci_anatomy_2010.owl"),
    Anatomy_MA_2009_rpd("Ant1_rpd", "file:///Z:/DataSet/anatomy/mouse_anatomy_2009_rpd.owl"),
    Anatomy_MA_2010_rpd("Ant1_rpd", "file:///Z:/DataSet/anatomy/mouse_anatomy_2010_rpd.owl"),
    Anatomy_NCI_2009_rpd("Ant1_rpd", "file:///Z:/DataSet/anatomy/nci_anatomy_2009_rpd.owl"),
    Anatomy_NCI_2010_rpd("Ant1_rpd", "file:///Z:/DataSet/anatomy/nci_anatomy_2010_rpd.owl"),
    Anatomy_PRA("AntPRA", "file:///Z:/DataSet/anatomy/PRA.rdf"),
    Anatomy_GKB("AntGKB", "file:///Z:/DataSet/anatomy/gkb.rdf"),

    /**
     * about Joint ontology
     */
    Joint_MA("Jt1", "file:///Z:/DataSet/joint/joint_mouse_anatomy.owl"),
    Joint_NCI("Jt2", "file:///Z:/DataSet/joint/joint_nci_anatomy.owl"),
    Joint_MA_rpd("Jt1_rpd", "file:///Z:/DataSet/joint/joint_mouse_anatomy_rpd.owl"),
    Joint_NCI_rpd("Jt2_rpd", "file:///Z:/DataSet/joint/joint_nci_anatomy_rpd.owl"),
    Joint_PRA("JtPRA", "Z:/DataSet/joint/PRA.rdf"),

    
    /**
     * about Behavior ontology
     */
    Behavior_GO("Bh1", "file:///Z:/DataSet/kitdb/behavior/behavior_GO_1.owl"),
    Behavior_SO("Bh2", "file:///Z:/DataSet/kitdb/behavior/behavior_SO_2.owl"),
    Behavior_PRA_C("BhPRAC", "Z:/DataSet/kitdb/behavior/PRA/behavior_C.rdf"),
    Behavior_PRA_I("BhPRAI", "Z:/DataSet/kitdb/behavior/PRA/behavior_I.rdf"),


    /**
     * about Defense ontology
     */
    Defense_GO("Df1", "file:///Z:/DataSet/kitdb/defense/defense_GO_1.owl"),
    Defense_SO("Df2", "file:///Z:/DataSet/kitdb/defense/defense_SO_2.owl"),
    Defense_PRA_C("DfPRAC", "Z:/DataSet/kitdb/defense/PRA/defense_C.rdf"),
    Defense_PRA_I("DfPRAI", "Z:/DataSet/kitdb/defense/PRA/defense_I.rdf"),



    /**
     * about Ear ontology
     */
    Ear_MA("Ear1", "file:///Z:/DataSet/kitdb/ear/ear_MA_1.owl"),
    Ear_MeSH("Ear2", "file:///Z:/DataSet/kitdb/ear/ear_MeSH_2.owl"),
    Ear_PRA_C("EarPRAC", "Z:/DataSet/kitdb/ear/PRA/ear_C.rdf"),
    Ear_PRA_I("EarPRAI", "Z:/DataSet/kitdb/ear/PRA/ear_I.rdf"),


    /**
     * about Nose ontology
     */
    Nose_GO("Nose1", "file:///Z:/DataSet/kitdb/nose/nose_MA_1.owl"),
    Nose_SO("Nose2", "file:///Z:/DataSet/kitdb/nose/nose_MeSH_2.owl"),
    Nose_PRA_C("NosePRAC", "Z:/DataSet/kitdb/nose/PRA/nose_C.rdf"),
    Nose_PRA_I("NosePRAI", "Z:/DataSet/kitdb/nose/PRA/nose_I.rdf"),

    /**
     * about Eye ontology
     */
    Eye_GO("Eye1", "file:///Z:/DataSet/kitdb/eye/eye_MA_1.owl"),
    Eye_SO("Eye2", "file:///Z:/DataSet/kitdb/eye/eye_MeSH_2.owl"),
    Eye_PRA_C("EyePRAC", "Z:/DataSet/kitdb/eye/PRA/eye_C.rdf"),
    Eye_PRA_I("EyePRAI", "Z:/DataSet/kitdb/eye/PRA/eye_I.rdf"),

    Gene_GO("Go1", "file:///Z:/DataSet/geneo/biological_process.owl");

    private String id;
    private String url;
    
    enumOnto(String id, String url) {
        this.id = id;
        this.url = url;
    }

    /**
     * Get the Id of the variable
     * @return the Id of the variable
     */
    public String getId() {
        return id;
    }

    /**
     * Get the URL of the variable
     * @return the URL of the variable
     */
    public String getUrl() {
        return url;
    }

}