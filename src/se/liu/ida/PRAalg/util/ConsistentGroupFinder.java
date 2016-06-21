package se.liu.ida.PRAalg.util;

import java.util.Vector;
import java.util.logging.Logger;
import se.liu.ida.sambo.util.Pair;

public class ConsistentGroupFinder extends GaAlg {

    private static Logger logger = Logger.getLogger(ConsistentGroupFinder.class.getName());
    
    Vector<Pair> suggestions;
    ConsistentChecker consisChecker;
    boolean[][] consisMatrix;


    public ConsistentGroupFinder(ConsistentChecker consisChecker, Vector<Pair> suggestions) {
        this.consisChecker = consisChecker;
        this.suggestions = suggestions;
        this.initalize();
    }

    public boolean isConsistent() {
        int len = suggestions.size();
        int[] choose = new int[len];
        // choose all
        for (int i = 0; i < len; i++) {
            choose[i] = 1;
        }
        return this.satisfyCondition(choose);
    }

    public Vector<Pair> getConsistentGroup() {
        if (isConsistent()) {
            return suggestions;
        }
        int[] choose = super.StartFind(10);
        this.noteChooseResult(choose);
        Vector<Pair> result = new Vector<Pair>();
        String debug_info = "Unchosen suggestion for consisSugs : \n";
        for (int i = 0; i < choose.length; i++) {
            if (choose[i] > 0.5) {
                result.add(suggestions.elementAt(i));
            } else {
                debug_info += suggestions.elementAt(i).toString() + "\n";
            }
        }
        logger.info(debug_info);
        return result;
    }

    private void initalize() {
        String debug_info = "Unconsistent pair of suggestions : \n";
        int num = suggestions.size();
        super.initalize(num);
        consisMatrix = new boolean[num][num];
        for (int i = 0; i < num; i++) {
            for (int j = i + 1; j < num; j++) {
                Pair sug1 = suggestions.elementAt(i);
                Pair sug2 = suggestions.elementAt(j);
                consisMatrix[i][j] = consisChecker.isConsistent(sug1, sug2);
                consisMatrix[j][i] = consisMatrix[i][j];
                if (!consisMatrix[i][j]) {
                    debug_info += sug1.toString() + " <-||-> " + sug2.toString() + "\n";
                }
            }
        }
        logger.info(debug_info);
//        logger.fine(this.debug_MatrixInfo());
    }

    private String debug_MatrixInfo() {
        String rt = "\n";
        int num = suggestions.size();
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                rt += consisMatrix[i][j] + " ";
            }
            rt += "\n";
        }
        return rt;
    }

    protected boolean satisfyCondition(int[] choose) {
        int len = consisMatrix.length;
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (choose[i] > 0.5 && choose[j] > 0.5) {
                    if (!consisMatrix[i][j]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * this function used to calculate the fitness value for a suggestion group.
     * the fitness value is calculated by using the formed complete graph.
     * Fitness = number of consistent edges - (number of inconsistent edges * number of vertexes)
     *
     * @param choose 
     * @return 				the consistent fitness value for a given suggestion group
     */
    protected double getFitnessValue(int[] choose) {
        int sugNum = consisMatrix.length;
        int inconsisPairNum = 0;
        int consisPairNum = 0;
        int chosenNum = 0;
        for (int i = 0; i < sugNum; i++) {
            if (choose[i] > 0.5) {
                chosenNum++;
            }
            for (int j = i + 1; j < sugNum; j++) {
                if (choose[j] > 0.5) {
                    if (!consisMatrix[i][j]) {
                        inconsisPairNum++;
                    } else {
                        consisPairNum++;
                    }
                }
            }
        }
        double fitnessValue = consisPairNum - inconsisPairNum * chosenNum + chosenNum;
        logger.finer("Fitness value : " + consisPairNum + "-" + inconsisPairNum + "*" + chosenNum + " + " +  chosenNum + " = " + fitnessValue);
        return fitnessValue;
    }

    private void noteChooseResult(int[] result) {
        int chooseNum = 0;
        String infor = "The result of consistent group is : \n";
        for (int i = 0; i < result.length; i++) {
            infor += (result[i] + " ");
            if (result[i] > 0.5) {
                chooseNum++;
            }
        }
        logger.fine("The number of suggestions in consistent group : " + chooseNum);
        logger.fine(infor);
    }
}
