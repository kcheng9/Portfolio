package project2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return a String representing its
     * answer to the question: "1", "2", "3", "4", "5", or "6". These Strings
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName().
     * 
     * In addition to returning your answer at the end of the method, your Agent
     * may also call problem.checkAnswer(String givenAnswer). The parameter
     * passed to checkAnswer should be your Agent's current guess for the
     * problem; checkAnswer will return the correct answer to the problem. This
     * allows your Agent to check its answer. Note, however, that after your
     * agent has called checkAnswer, it will *not* be able to change its answer.
     * checkAnswer is used to allow your Agent to learn from its incorrect
     * answers; however, your Agent cannot change the answer to a question it
     * has already answered.
     * 
     * If your Agent calls checkAnswer during execution of Solve, the answer it
     * returns will be ignored; otherwise, the answer returned at the end of
     * Solve will be taken as your Agent's answer to this problem.
     * 
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public String Solve(RavensProblem problem) {
    	String soln = "1";
    	
    	System.out.println("program start");
    	System.out.println(problem.getProblemType());
    	if (problem.getProblemType().equals("2x1")){
	        HashMap<String, RavensFigure> figures = problem.getFigures();
	        RavensFigure figureA = figures.get("A");
	        RavensFigure figureB = figures.get("B");
	        RavensFigure figureC = figures.get("C");
	        RavensFigure figure1 = figures.get("1");
	        RavensFigure figure2 = figures.get("2");
	        RavensFigure figure3 = figures.get("3");
	        RavensFigure figure4 = figures.get("4");
	        RavensFigure figure5 = figures.get("5");
	        RavensFigure figure6 = figures.get("6");
	        
	        ArrayList<RavensObject> figureAObjects = figureA.getObjects();
	        ArrayList<RavensObject> figureBObjects = figureB.getObjects();
	        ArrayList<RavensObject> figureCObjects = figureC.getObjects();
	        ArrayList<RavensObject> figure1Objects = figure1.getObjects();
	        ArrayList<RavensObject> figure2Objects = figure2.getObjects();
	        ArrayList<RavensObject> figure3Objects = figure3.getObjects();
	        ArrayList<RavensObject> figure4Objects = figure4.getObjects();
	        ArrayList<RavensObject> figure5Objects = figure5.getObjects();
	        ArrayList<RavensObject> figure6Objects = figure6.getObjects();
	        
	        
	        ArrayList<RavensObject> correspondingObjectsAB = getCorrespondingObjectsAB(figureAObjects, figureBObjects);
	        ////system.out.println(correspondingObjectsAB);
	        
	        ArrayList<RavensObject> correspondingObjectsAC = getCorrespondingObjectsAC(figureAObjects, figureCObjects);
	
	        ////system.out.println(correspondingObjectsAC);
	        //ArrayList<RavensObject> correspondingObjectsC5 = getCorrespondingObjectsAB(figureCObjects, figure5Objects);
	        ////system.out.println(correspondingObjectsC5);
	        
	        ////system.out.println(correspondingObjectsAB);
	        //system.out.println("corrAB"+correspondingObjectsAB);
	        //system.out.println("corrA"+putOrderedObjectsFirst(correspondingObjectsAB));
	        //system.out.println("corrB"+putOrderedObjectsSecond(correspondingObjectsAB));
	        
	        //RavensObject deleted = new RavensObject("deleted");
	        ////system.out.println(deleted.getName());
	        
	        ////system.out.println("deletedattr"+getAttributes(deleted));
	        
	        ////system.out.println("corrBAttr"+getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsAB)));
	        ////system.out.println(correspondingObjectsAC);
	        ////system.out.println(putOrderedObjectsSecond(correspondingObjectsAC));
	        ArrayList<String> attr1 = (getAllObjectAttributes(putOrderedObjectsFirst(correspondingObjectsAB)));
	        
	        ArrayList<String> attr2 = (getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsAB)));
	        
	        ArrayList<String> attrc = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsAC));
	        
	        ////system.out.println("what" + putOrderedObjectsSecond(correspondingObjectsAB));
	        
	        //System.out.println("attr1"+attr1);
	        //System.out.println("attr2"+attr2);
	        //ArrayList<String> attrfigure5 = getAllObjectAttributes(correspondingObjectsC5.get(1));
	        ArrayList<String> compare12 = compareAttributesAB(attr1,attr2);
	        
	        //System.out.println("compare12"+compare12);
	        ArrayList<String> solnShoulds12 = solnShouldAttrs(attrc, attr2, compare12);
	        
	        soln = tryAllProposedSolns(solnShoulds12, figureCObjects, figure1Objects, figure2Objects, figure3Objects,
	        		figure4Objects, figure5Objects, figure6Objects);
	        
	        
	        ////system.out.println(getAllObjectAttributes(figureA));
	        
	        //getCorrespondingObjectsAB(figureAObjects, figureBObjects);
	        
	        ////system.out.println("CorrespondingObjectsAB" + correspondingObjectsAB.toString());
	        
	        ////system.out.println("compare12"+compare12);
	        ////system.out.println("solnShoulds"+solnShoulds12);
	        
	        ////system.out.println(soln);
	        ////system.out.println(problem.checkAnswer(soln));
    	}
    	
    	else{
    		HashMap<String, RavensFigure> figures = problem.getFigures();
	        RavensFigure figureA = figures.get("A");
	        RavensFigure figureB = figures.get("B");
	        RavensFigure figureC = figures.get("C");
	        RavensFigure figure1 = figures.get("1");
	        RavensFigure figure2 = figures.get("2");
	        RavensFigure figure3 = figures.get("3");
	        RavensFigure figure4 = figures.get("4");
	        RavensFigure figure5 = figures.get("5");
	        RavensFigure figure6 = figures.get("6");
	        
	        ArrayList<RavensObject> figureAObjects = figureA.getObjects();
	        ArrayList<RavensObject> figureBObjects = figureB.getObjects();
	        ArrayList<RavensObject> figureCObjects = figureC.getObjects();
	        ArrayList<RavensObject> figure1Objects = figure1.getObjects();
	        ArrayList<RavensObject> figure2Objects = figure2.getObjects();
	        ArrayList<RavensObject> figure3Objects = figure3.getObjects();
	        ArrayList<RavensObject> figure4Objects = figure4.getObjects();
	        ArrayList<RavensObject> figure5Objects = figure5.getObjects();
	        ArrayList<RavensObject> figure6Objects = figure6.getObjects();
    		
    		ArrayList<RavensObject> correspondingObjectsAB = getCorrespondingObjectsAB(figureAObjects, figureBObjects);
	        ArrayList<RavensObject> correspondingObjectsAC = getCorrespondingObjectsAC(figureAObjects, figureCObjects);
	        ArrayList<RavensObject> correspondingObjectsBC = getCorrespondingObjectsAB(figureBObjects, figureCObjects);
	        
	        System.out.println("corresAB"+correspondingObjectsAB);
	        System.out.println("corresAC"+correspondingObjectsAC);
	        
	        ArrayList<RavensObject> relateBC = relateBC(correspondingObjectsAB, correspondingObjectsAC);
	        System.out.println("relateBC"+relateBC);
	        
	        ArrayList<String> attrABA = getAllObjectAttributes(putOrderedObjectsFirst(correspondingObjectsAB));
	        ArrayList<String> attrABB = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsAB));
	        ArrayList<String> attrACA = getAllObjectAttributes(putOrderedObjectsFirst(correspondingObjectsAC));
	        ArrayList<String> attrACC = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsAC));
	        ArrayList<String> attrBCB = getAllObjectAttributes(putOrderedObjectsFirst(correspondingObjectsBC));
	        ArrayList<String> attrBCC = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsBC));
	        
	/*        System.out.println("attrABA"+attrABA);
	        System.out.println("attrABB"+attrABB);
	        System.out.println("attrACA"+attrACA);
	        System.out.println("attrACC"+attrACC);*/
/*	        System.out.println("attrBCB"+attrBCB);
	        System.out.println("attrBCC"+attrBCC);*/
	        
	        
	        //somehow get them to compare A objects to B objects and C objects IN ORDER
	        //THEN do the matchup of the attributes
	        
	        
	        ArrayList<RavensObject> relateBs = relateBs(relateBC);
	        ArrayList<RavensObject> relateAs = relateAs(relateBs,correspondingObjectsAB);
	        ArrayList<RavensObject> relateCs = relateCs(relateBC);
	        
	        ArrayList<String> attrAs = getAllObjectAttributes(relateAs);
	        ArrayList<String> attrBs = getAllObjectAttributes(relateBs);
	        ArrayList<String> attrCs = getAllObjectAttributes(relateCs);
	        
	        System.out.println("relateAs"+relateAs);
	        System.out.println("relateBs"+relateBs);
	        System.out.println("relateCs"+relateCs);
	        
	        System.out.println("attrAs"+attrAs);
	        System.out.println("attrBs"+attrBs);
	        System.out.println("attrCs"+attrCs);
	        
	        ArrayList<String> compareAB = compareAttributesAB2x2(attrABA,attrABB);
	        ArrayList<String> compareAC = compareAttributesAB2x2(attrACA,attrACC);
	        //ArrayList<String> compareBC = compareAttributesAB2x2(attrBCB,attrBCC);
	        ArrayList<String> compareAsBs = compareAttributesAB2x2(attrAs, attrBs);
	        ArrayList<String> compareAsCs = compareAttributesAB2x2(attrAs, attrCs);
	        
	        System.out.println("compareAsBs"+compareAsBs);
	        System.out.println("compareAsCs"+compareAsCs);
	        
	        //System.out.println("compareAB"+compareAB);
/*	        System.out.println("compareAC"+compareAC);
	        System.out.println("compareBC"+compareBC);*/
	        
	        /*ArrayList<String> solnShouldsAB = solnShouldAttrs(attrABB, attrABA, compareAB);
	        ArrayList<String> solnShouldsAC = solnShouldAttrs(attrACC, attrABB, compareAC);
	        ArrayList<String> solnShouldsBC = solnShouldAttrs(attrBCC, attrBCB, compareBC);
	        */
/*	        System.out.println("solnShouldsAB"+solnShouldsAB);
	        System.out.println("solnShouldsAC"+solnShouldsAC);
	        System.out.println("solnShouldsBC"+solnShouldsBC);*/
/*	        
	        ArrayList<String> solnShouldsNew = solnShoulds2x2(compareAB, compareAC, attrABA, attrABB,
	        		attrACA, attrACC);*/
	        
	        /*System.out.println("solnShouldsNew"+solnShouldsNew);*/
	        
	        ArrayList<String> solnShoulds = solnShoulds2x2(compareAB, compareAC, attrABA, attrABB, attrACA, attrACC);
	        System.out.println("solnShoulds"+solnShoulds);
	        
	        ArrayList<String> solnShouldsRelated = solnShoulds2x2(compareAsBs, compareAsCs, attrAs, attrBs, attrAs, attrCs);
	        ArrayList<String> solnShouldsRelatedHelper = replacePositionalAttributes(solnShoulds, relateBC);
		    System.out.println("solnShouldsRelated"+solnShouldsRelatedHelper);
		    
		    //System.out.println(tryAllProposedSolns2x2(relateBC,solnShouldsRelatedHelper, figureCObjects, figure1Objects, figure2Objects,
		    		//figure3Objects, figure4Objects, figure5Objects, figure6Objects));
	        
	        
		    
	        //ArrayList<String> solnShouldsHelper = replacePositionalAttributes(solnShoulds, correspondingObjectsAB);
		    
	        /*ArrayList<String> solnShouldsHelper = replacePositionalAttributes(solnShoulds, relateBC);
	        
	        soln = tryAllProposedSolns(solnShouldsHelper, figureCObjects, figure1Objects, figure2Objects, figure3Objects, figure4Objects,
	        		figure5Objects, figure6Objects);    
	        */
		    
		    
		    soln = tryProposedSolns2x2(relateCs,solnShouldsRelatedHelper, figureCObjects, figure1Objects, figure2Objects, figure3Objects,
		    		figure4Objects, figure5Objects, figure6Objects);
		    
		    
		    
	        //System.out.println("I got here");
/*	        soln = tryAllProposedSolns2x2(relateBC, solnShouldsHelper, figureCObjects, figure1Objects, figure2Objects, figure3Objects,
	        		figure4Objects, figure5Objects, figure6Objects);*/
	        
	        
	        
	        
	        //soln = tryAllProposedSolns2x2(solnShouldsAB, solnShouldsAC, figureBObjects, figureCObjects,
	        	//	figure1Objects, figure2Objects, figure3Objects, figure4Objects, figure5Objects, figure6Objects);
	        
	        
	        /*if (solnShouldsAB.equals(solnShouldsAC)){
	        	ArrayList<String> perfectSoln = solnShouldsAB;
	        	soln = tryAllProposedSolns(perfectSoln, figureCObjects, figure1Objects, figure2Objects, figure3Objects,
		        		figure4Objects, figure5Objects, figure6Objects);
	        }
	        else{
	        	soln = tryAllProposedSolns(solnShouldsAB, figureCObjects, figure1Objects, figure2Objects, figure3Objects,
		        		figure4Objects, figure5Objects, figure6Objects);
	        }*/
	        //solnShoulds = solnShouldAttrs2x2(compareAB, compareAC);
	        
//	        String soln = tryAllProposedSolns(solnShoulds12, figureCObjects, figure1Objects, figure2Objects, figure3Objects,
//	        		figure4Objects, figure5Objects, figure6Objects);
    	}
        return soln;
    }
    
/*    private ArrayList<String> compareAttributesNew(ArrayList<String> attrA, ArrayList<String> attrB){
    	ArrayList<String> compareAttr = new ArrayList<String>();
    	
    	
    }*/
    private String tryProposedSolns2x2(ArrayList<RavensObject> relateCs, ArrayList<String> solnShoulds, ArrayList<RavensObject> figureCObjects, ArrayList<RavensObject> figure1Objects,
    		ArrayList<RavensObject> figure2Objects, ArrayList<RavensObject> figure3Objects, ArrayList<RavensObject> figure4Objects,
    		ArrayList<RavensObject> figure5Objects, ArrayList<RavensObject> figure6Objects){
		
    	String answer = "none";
    	
    	//system.out.println(solnShoulds);
    	
    	ArrayList<RavensObject> correspondingObjectsC1 = getCorrespondingObjectsAB(figureCObjects, figure1Objects);
    	ArrayList<RavensObject> correspondingObjectsC2 = getCorrespondingObjectsAB(figureCObjects, figure2Objects);
    	ArrayList<RavensObject> correspondingObjectsC3 = getCorrespondingObjectsAB(figureCObjects, figure3Objects);
    	ArrayList<RavensObject> correspondingObjectsC4 = getCorrespondingObjectsAB(figureCObjects, figure4Objects);
    	ArrayList<RavensObject> correspondingObjectsC5 = getCorrespondingObjectsAB(figureCObjects, figure5Objects);
    	ArrayList<RavensObject> correspondingObjectsC6 = getCorrespondingObjectsAB(figureCObjects, figure6Objects);
    	
    	correspondingObjectsC1 = relateCs(correspondingObjectsC1);
    	correspondingObjectsC2 = relateCs(correspondingObjectsC2);
    	correspondingObjectsC3 = relateCs(correspondingObjectsC3);
    	correspondingObjectsC4 = relateCs(correspondingObjectsC4);
    	correspondingObjectsC5 = relateCs(correspondingObjectsC5);
    	correspondingObjectsC6 = relateCs(correspondingObjectsC6);
    	
    	System.out.println("corrC1"+correspondingObjectsC1);
    	System.out.println("corrC2"+correspondingObjectsC2);
    	System.out.println("corrC3"+correspondingObjectsC3);
    	System.out.println("corrC4"+correspondingObjectsC4);
    	System.out.println("corrC5"+correspondingObjectsC5);
    	System.out.println("corrC6"+correspondingObjectsC6);
    	
    	ArrayList<String> attrfigure1 = getAllObjectAttributes((correspondingObjectsC1));
    	ArrayList<String> attrfigure2 = getAllObjectAttributes((correspondingObjectsC2));
    	ArrayList<String> attrfigure3 = getAllObjectAttributes((correspondingObjectsC3));
    	ArrayList<String> attrfigure4 = getAllObjectAttributes((correspondingObjectsC4));
    	ArrayList<String> attrfigure5 = getAllObjectAttributes((correspondingObjectsC5));
    	ArrayList<String> attrfigure6 = getAllObjectAttributes((correspondingObjectsC6));
    	////system.out.println("corrC1"+correspondingObjectsC1);
    	////system.out.println("corrC2"+correspondingObjectsC2);
    	////system.out.println("corrC3"+correspondingObjectsC3);
    	////system.out.println("corrC4"+correspondingObjectsC4);
    	////system.out.println("corrC5"+correspondingObjectsC5);
    	////system.out.println("corrC6"+correspondingObjectsC6);
    	
    	////system.out.println("figure1"+attrfigure1);
    	////system.out.println("figure2"+attrfigure2);
    	////system.out.println("figure3"+attrfigure3);
    	////system.out.println("figure4"+attrfigure4);
    	////system.out.println("figure5"+attrfigure5);
    	////system.out.println("figure6"+attrfigure6);
    	
    	Integer comparison1 = compareSolutionShoulds(solnShoulds, attrfigure1);
    	Integer comparison2 = compareSolutionShoulds(solnShoulds, attrfigure2);
    	Integer comparison3 = compareSolutionShoulds(solnShoulds, attrfigure3);
    	Integer comparison4 = compareSolutionShoulds(solnShoulds, attrfigure4);
    	Integer comparison5 = compareSolutionShoulds(solnShoulds, attrfigure5);
    	Integer comparison6 = compareSolutionShoulds(solnShoulds, attrfigure6);
    	/*System.out.println("I got here");
    	System.out.println("attrfigureC1"+attrfigure1);
    	System.out.println("attrfigureC2"+attrfigure2);
    	System.out.println("attrfigureC3"+attrfigure3);
    	System.out.println("attrfigureC4"+attrfigure4);
    	System.out.println("attrfigureC5"+attrfigure5);
    	System.out.println("attrfigureC6"+attrfigure6);*/
    	ArrayList<Integer> compares = new ArrayList<Integer>();
    	compares.add(comparison1);
    	compares.add(comparison2);
    	compares.add(comparison3);
    	compares.add(comparison4);
    	compares.add(comparison5);
    	compares.add(comparison6);
    	
    	/*Integer solnShouldsSize = solnShoulds.size();
    	////system.out.println(solnShouldsSize);
    	
    	if (comparison1 == solnShouldsSize){
    		answer = "1";
    	}
    	else if (comparison2 == solnShouldsSize){
    		answer = "2";
    	}
    	else if (comparison3 == solnShouldsSize){
    		answer = "3";
    	}
    	else if (comparison4 == solnShouldsSize){
    		answer = "4";
    	}
    	else if (comparison5 == solnShouldsSize){
    		answer = "5";
    	}
    	else if (comparison6 == solnShouldsSize){
    		answer = "6";
    	}
    	else{*/
    		////system.out.println("Solution not found, next best?");
    		////system.out.println(compares);
    		////system.out.println(Collections.max(compares));
    		if (comparison1 == Collections.max(compares)){
    			answer = "1";
    		}
    		else if (comparison2 == Collections.max(compares)){
    			answer = "2";
    		}
    		else if (comparison3 == Collections.max(compares)){
    			answer = "3";
    		}
    		else if (comparison4 == Collections.max(compares)){
    			answer = "4";
    		}
    		else if (comparison5 == Collections.max(compares)){
    			answer = "5";
    		}
    		else if (comparison6 == Collections.max(compares)){
    			answer = "6";
    		}
    	
    		System.out.println(compares);
    	return answer;
	}
    private ArrayList<RavensObject> relateAs(ArrayList<RavensObject> relateBs, ArrayList<RavensObject> correspondingObjectsAB){
    	ArrayList<RavensObject> relateAs = new ArrayList<RavensObject>();
    	for (int i = 0; i < relateBs.size(); i++){
    		for (int j = 0; j < correspondingObjectsAB.size(); j++){
    			if (relateBs.get(i).equals(correspondingObjectsAB.get(j))){
    				relateAs.add(correspondingObjectsAB.get(j-1));
    			}
    		}
    	}
    	
    	return relateAs;
    }
    
    private ArrayList<RavensObject> relateBs(ArrayList<RavensObject> relateBC){
    	ArrayList<RavensObject> relateBs = new ArrayList<RavensObject>();
    	
    	for (int i = 0; i<relateBC.size(); i+=2){
    		if (relateBC.get(i).getName().equals("no item")){
    			relateBs.add(relateBC.get(i+1));
    		}
    		else{
    			relateBs.add(relateBC.get(i));
    		}
    	}
    	
    	for (int j = relateBs.size()-1; j>=0; j--){
    		if (relateBs.get(j).getName().equals("no item")){
    			relateBs.remove(j);
    			relateBs.add(relateBC.get(j+1));
    		}
    	}
    	
    	return relateBs;
    }
    
    private ArrayList<RavensObject> relateCs(ArrayList<RavensObject>relateBC){
    	ArrayList<RavensObject> relateCs = new ArrayList<RavensObject>();
    	
    	for (int i = 1; i<relateBC.size(); i+=2){
    		relateCs.add(relateBC.get(i));
    	}
    	
    	for (int j = relateCs.size()-1; j>=0; j--){
    		if (relateCs.get(j).getName().equals("no item")){
    			relateCs.remove(j);
    		}
    	}
    	
    	return relateCs;
    }
    
    private ArrayList<RavensObject> relateBC(ArrayList<RavensObject> correspondingObjectsAB, ArrayList<RavensObject> correspondingObjectsAC){
    	ArrayList<RavensObject> relateBC = new ArrayList<RavensObject>();
    	for (int i = 0; i < correspondingObjectsAB.size(); i+=2){
    		for (int j = 0; j < correspondingObjectsAC.size(); j+=2){
    			if (correspondingObjectsAB.get(i).getName().equals("no item")){
    				relateBC.add(correspondingObjectsAB.get(i));
    				relateBC.add(correspondingObjectsAB.get(i+1));
    			}
    			if (correspondingObjectsAC.get(j).getName().equals("no item")){
    				relateBC.add(correspondingObjectsAC.get(j));
    				relateBC.add(correspondingObjectsAC.get(j+1));
    			}
    			if (correspondingObjectsAB.get(i).equals(correspondingObjectsAC.get(j))){
    				relateBC.add(correspondingObjectsAB.get(i+1));
    				relateBC.add(correspondingObjectsAC.get(j+1));
    			}
    		}
    	}
    	for (int k = relateBC.size()-1; k>=0; k-=2){
    		if (relateBC.get(k).getName().equals("deleted")){
    			relateBC.remove(k);
    			relateBC.remove(k-1);
    		}
    	}
    	for (int l = relateBC.size()-2; l>=0; l-=2){
    		if (relateBC.get(l).getName().equals("deleted")){
    			relateBC.remove(l+1);
    			relateBC.remove(l);
    		}
    	}
    	
    	
    	return relateBC;
    }
    
    private ArrayList<String> replacePositionalAttributes(ArrayList<String> attr, ArrayList<RavensObject> corresponder){
    	ArrayList<String> pos = new ArrayList<String>();
    	//Attributes will just be held at different indices of the ArrayList:
    	//[0] shape
    	//[1] fill
    	//[2] size 
    	//[3] inside
    	//[4] angle
    	//[5] above
    	//[6] left-of
    	//[7] vertical-flip
    	//[8] overlaps
    	
    	ArrayList<String> corresponderString = new ArrayList<String>();
    	
    	for (int j = 0; j < corresponder.size(); j++){
    		corresponderString.add(corresponder.get(j).getName());
    	}
    	
    	ArrayList<String> corresponderHelper = corresponderString;
    	//System.out.println("corresHelper"+corresponderHelper);
    	
    	for (int i = 3; i < attr.size(); i+=9){
    		if (!attr.get(i).equals("not here") && !attr.get(i).equals("no item") && !attr.get(i).equals("deleted") && !attr.get(i).equals("unchanged")){
    			//System.out.println("attr"+attr.get(i));
    			String[] poses = attr.get(i).split("[,]");
    			String corrHelper2 = null;
    			//System.out.println("poses"+Arrays.asList(poses));
    			//System.out.println(poses.length);
	    		for (int j = 0; j < poses.length; j++){
	    			if (corrHelper2 == null){
	    				corrHelper2=(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    			else{
	    				corrHelper2+=","+(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    		}
	    		attr.set(i, corrHelper2);
    		}
    	}

    	for (int i = 5; i < attr.size(); i+=9){
    		if (!attr.get(i).equals("not here") && !attr.get(i).equals("no item") && !attr.get(i).equals("deleted") && !attr.get(i).equals("unchanged")){
    			//System.out.println("attr"+attr.get(i));
    			String[] poses = attr.get(i).split("[,]");
    			String corrHelper2 = null;
    			//System.out.println("poses"+Arrays.asList(poses));
    			//System.out.println(poses.length);
	    		for (int j = 0; j < poses.length; j++){
	    			if (corrHelper2 == null){
	    				corrHelper2=(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    			else{
	    				corrHelper2+=","+(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    		}
	    		attr.set(i, corrHelper2);
    		}
    	}
    	
    	for (int i = 6; i < attr.size(); i+=9){
    		if (!attr.get(i).equals("not here") && !attr.get(i).equals("no item") && !attr.get(i).equals("deleted") && !attr.get(i).equals("unchanged")){
    			//System.out.println("attr"+attr.get(i));
    			String[] poses = attr.get(i).split("[,]");
    			String corrHelper2 = null;
    			//System.out.println("poses"+Arrays.asList(poses));
    			//System.out.println(poses.length);
	    		for (int j = 0; j < poses.length; j++){
	    			if (corrHelper2 == null){
	    				corrHelper2=(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    			else{
	    				corrHelper2+=","+(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    		}
	    		attr.set(i, corrHelper2);
    		}
    	}
    	
    	for (int i = 8; i < attr.size(); i+=9){
    		if (!attr.get(i).equals("not here") && !attr.get(i).equals("no item") && !attr.get(i).equals("deleted") && !attr.get(i).equals("unchanged")){
    			//System.out.println("attr"+attr.get(i));
    			String[] poses = attr.get(i).split("[,]");
    			String corrHelper2 = null;
    			//System.out.println("poses"+Arrays.asList(poses));
    			//System.out.println(poses.length);
	    		for (int j = 0; j < poses.length; j++){
	    			if (corrHelper2 == null){
	    				corrHelper2=(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    			else{
	    				corrHelper2+=","+(corresponderHelper.get(corresponderHelper.indexOf(poses[j])+1));
	    			}
	    		}
	    		attr.set(i, corrHelper2);
    		}
    	}
    	
    	System.out.println("newAttr"+attr);
    	pos = attr;
    	return pos;
    }
    
    private ArrayList<String> solnShoulds2x2(ArrayList<String> compareAB, ArrayList<String> compareAC, ArrayList<String> attrABA,
    		ArrayList<String> attrABB, ArrayList<String> attrACA, ArrayList<String> attrACC){
    	
    	ArrayList<String> solnShoulds = new ArrayList<String>();
    	System.out.println("compareAB"+compareAB);
    	System.out.println("compareAC"+compareAC);
    	
    	
    	
    	
    	
    	//The first part I'll write only works if the # of attributes in compareAB and compareAC are the same.
    	if (compareAB.size() <= compareAC.size()){
        	//If the attribute is unchanged in one and changed in the other, it will take the changed attribute.
    		for (int i = 0; i < compareAB.size(); i++){
				if (compareAB.get(i).equals("unchanged") && !compareAC.get(i).equals("unchanged")){
					solnShoulds.add(attrACC.get(i));
				}
				else if (compareAB.get(i).equals("not here") && compareAC.get(i).equals("not here")){
					solnShoulds.add("not here");
				}
				else if (!compareAB.get(i).equals("unchanged") && compareAC.get(i).equals("unchanged")){
					solnShoulds.add(attrABB.get(i));
				}
				else if (!attrABB.get(i).equals("unchanged")&& !attrABB.get(i).equals("deleted")){
					if ((i==4 || i==13 || i==22) & !attrABB.get(i).equals("0")){
						if (Integer.parseInt(compareAB.get(i)) + Integer.parseInt(attrACC.get(i)) < 360){
			    			solnShoulds.add(Integer.toString(Integer.parseInt(compareAB.get(i)) + Integer.parseInt(attrACC.get(i))));
			    		}
			    		else{
			    			solnShoulds.add(Integer.toString(Integer.parseInt(compareAB.get(i)) + Integer.parseInt(attrACC.get(i))-360));
			    		}
					}
					else{
						solnShoulds.add(attrABB.get(i));
					}
				}
				else if (attrABB.get(i).equals(attrACC.get(i))&& !attrABB.get(i).equals("deleted")){
					solnShoulds.add(attrABB.get(i));
				}
				else if (attrABB.get(i).equals("deleted")){
					
				}
				else{
					solnShoulds.add("not here");
				}
    		}
    	}
    	
    	if (compareAB.size() < compareAC.size()){
    		ArrayList<String> solnShouldsHelper = solnShoulds;
    		int totalObj = compareAC.size();
    		int bObj = compareAB.size();
    		int needed = (totalObj-bObj)/9;
    		int nextVal = solnShoulds.size()/9;
    		for (int i = nextVal; i <= needed; i++){
    			solnShoulds.addAll(solnShouldsHelper);
    		}
    	}
    	
    	if (compareAB.size() > compareAC.size()){
    		for (int i = 0; i < compareAC.size(); i++){
				if (compareAB.get(i).equals("unchanged") && !compareAC.get(i).equals("unchanged")){
					solnShoulds.add(attrACC.get(i));
				}
				else if (compareAB.get(i).equals("not here") && compareAC.get(i).equals("not here")){
					solnShoulds.add("not here");
				}
				else if (!compareAB.get(i).equals("unchanged") && compareAC.get(i).equals("unchanged")){
					solnShoulds.add(attrABB.get(i));
				}
				else if (!attrABB.get(i).equals("unchanged") && !attrABB.get(i).equals("deleted") && !attrABB.get(i).equals("not here")){
					if ((i==4 || i==13 || i==22) && !attrABB.get(i).equals("0")){
						if (Integer.parseInt(compareAB.get(i)) + Integer.parseInt(attrACC.get(i)) < 360){
			    			solnShoulds.add(Integer.toString(Integer.parseInt(compareAB.get(i)) + Integer.parseInt(attrACC.get(i))));
			    		}
			    		else{
			    			solnShoulds.add(Integer.toString(Integer.parseInt(compareAB.get(i)) + Integer.parseInt(attrACC.get(i))-360));
			    		}
					}
					else{
						solnShoulds.add(attrABB.get(i));
					}
				}
				else if (attrABB.get(i).equals(attrACC.get(i))&& !attrABB.get(i).equals("deleted")){
					solnShoulds.add(attrABB.get(i));
				}
				else if (attrABB.get(i).equals("deleted")){
					
				}
				else{
					solnShoulds.add("not here");
				}
    		}
    		
    		int nextValue = solnShoulds.size();
    		
    		for (int j = nextValue; j < compareAB.size(); j++){
    			solnShoulds.add(compareAB.get(j));
    		}
    	}
    	
    	
    	
    	return solnShoulds;
    }
    
    private ArrayList<RavensObject> corresCsols(ArrayList<RavensObject> corresBC, ArrayList<RavensObject> corresCs){
    	ArrayList<RavensObject> corresCsols = new ArrayList<RavensObject>();
		
    	for (int i = 1; i<corresBC.size(); i+=2){
    		
    		if (corresCs.contains(corresBC.get(i))){
    			int indexC = corresCs.indexOf(corresBC.get(i));
    			corresCsols.add(corresCs.get(indexC));
    			corresCsols.add(corresCs.get(indexC+1));
    		}
    	}
    	//System.out.println("corresCsols"+corresCsols);
    	return corresCsols;
    }
    

	private String tryAllProposedSolns2x2(ArrayList<RavensObject> corresBC, ArrayList<String> solnShoulds, ArrayList<RavensObject> figureCObjects, ArrayList<RavensObject> figure1Objects,
    		ArrayList<RavensObject> figure2Objects, ArrayList<RavensObject> figure3Objects, ArrayList<RavensObject> figure4Objects,
    		ArrayList<RavensObject> figure5Objects, ArrayList<RavensObject> figure6Objects){
		
    	String answer = "none";
    	
    	//system.out.println(solnShoulds);
    	
    	ArrayList<RavensObject> correspondingObjectsC1 = getCorrespondingObjectsAB(figureCObjects, figure1Objects);
    	ArrayList<RavensObject> correspondingObjectsC2 = getCorrespondingObjectsAB(figureCObjects, figure2Objects);
    	ArrayList<RavensObject> correspondingObjectsC3 = getCorrespondingObjectsAB(figureCObjects, figure3Objects);
    	ArrayList<RavensObject> correspondingObjectsC4 = getCorrespondingObjectsAB(figureCObjects, figure4Objects);
    	ArrayList<RavensObject> correspondingObjectsC5 = getCorrespondingObjectsAB(figureCObjects, figure5Objects);
    	ArrayList<RavensObject> correspondingObjectsC6 = getCorrespondingObjectsAB(figureCObjects, figure6Objects);
    	
    	correspondingObjectsC1 = corresCsols(corresBC, correspondingObjectsC1);
    	correspondingObjectsC2 = corresCsols(corresBC, correspondingObjectsC2);
    	correspondingObjectsC3 = corresCsols(corresBC, correspondingObjectsC3);
    	correspondingObjectsC4 = corresCsols(corresBC, correspondingObjectsC4);
    	correspondingObjectsC5 = corresCsols(corresBC, correspondingObjectsC5);
    	correspondingObjectsC6 = corresCsols(corresBC, correspondingObjectsC6);
    	
    	ArrayList<String> attrfigure1 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC1));
    	ArrayList<String> attrfigure2 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC2));
    	ArrayList<String> attrfigure3 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC3));
    	ArrayList<String> attrfigure4 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC4));
    	ArrayList<String> attrfigure5 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC5));
    	ArrayList<String> attrfigure6 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC6));
    	////system.out.println("corrC1"+correspondingObjectsC1);
    	////system.out.println("corrC2"+correspondingObjectsC2);
    	////system.out.println("corrC3"+correspondingObjectsC3);
    	////system.out.println("corrC4"+correspondingObjectsC4);
    	////system.out.println("corrC5"+correspondingObjectsC5);
    	////system.out.println("corrC6"+correspondingObjectsC6);
    	
    	////system.out.println("figure1"+attrfigure1);
    	////system.out.println("figure2"+attrfigure2);
    	////system.out.println("figure3"+attrfigure3);
    	////system.out.println("figure4"+attrfigure4);
    	////system.out.println("figure5"+attrfigure5);
    	////system.out.println("figure6"+attrfigure6);
    	
    	Integer comparison1 = compareSolutionShoulds(solnShoulds, attrfigure1);
    	Integer comparison2 = compareSolutionShoulds(solnShoulds, attrfigure2);
    	Integer comparison3 = compareSolutionShoulds(solnShoulds, attrfigure3);
    	Integer comparison4 = compareSolutionShoulds(solnShoulds, attrfigure4);
    	Integer comparison5 = compareSolutionShoulds(solnShoulds, attrfigure5);
    	Integer comparison6 = compareSolutionShoulds(solnShoulds, attrfigure6);
    	/*System.out.println("I got here");
    	System.out.println("attrfigureC1"+attrfigure1);
    	System.out.println("attrfigureC2"+attrfigure2);
    	System.out.println("attrfigureC3"+attrfigure3);
    	System.out.println("attrfigureC4"+attrfigure4);
    	System.out.println("attrfigureC5"+attrfigure5);
    	System.out.println("attrfigureC6"+attrfigure6);*/
    	ArrayList<Integer> compares = new ArrayList<Integer>();
    	compares.add(comparison1);
    	compares.add(comparison2);
    	compares.add(comparison3);
    	compares.add(comparison4);
    	compares.add(comparison5);
    	compares.add(comparison6);
    	
    	/*Integer solnShouldsSize = solnShoulds.size();
    	////system.out.println(solnShouldsSize);
    	
    	if (comparison1 == solnShouldsSize){
    		answer = "1";
    	}
    	else if (comparison2 == solnShouldsSize){
    		answer = "2";
    	}
    	else if (comparison3 == solnShouldsSize){
    		answer = "3";
    	}
    	else if (comparison4 == solnShouldsSize){
    		answer = "4";
    	}
    	else if (comparison5 == solnShouldsSize){
    		answer = "5";
    	}
    	else if (comparison6 == solnShouldsSize){
    		answer = "6";
    	}
    	else{*/
    		////system.out.println("Solution not found, next best?");
    		////system.out.println(compares);
    		////system.out.println(Collections.max(compares));
    		if (comparison1 == Collections.max(compares)){
    			answer = "1";
    		}
    		else if (comparison2 == Collections.max(compares)){
    			answer = "2";
    		}
    		else if (comparison3 == Collections.max(compares)){
    			answer = "3";
    		}
    		else if (comparison4 == Collections.max(compares)){
    			answer = "4";
    		}
    		else if (comparison5 == Collections.max(compares)){
    			answer = "5";
    		}
    		else if (comparison6 == Collections.max(compares)){
    			answer = "6";
    		}
    	
    		System.out.println(compares);
    	return answer;
	}
		

    private ArrayList<RavensObject> putOrderedObjectsFirst(ArrayList<RavensObject> correspondingObjectsFirst){
    	ArrayList<RavensObject> orderedObjectsFirst = new ArrayList<RavensObject>();
    	////system.out.println(correspondingObjectsFirst.size());
    	for (int i = 0; i <= correspondingObjectsFirst.size()-1; i+=2){
//    		if (correspondingObjectsFirst.get(i+1).getName().equals("deleted")){
//    			
//    		}
//    		else{
    			orderedObjectsFirst.add(correspondingObjectsFirst.get(i));
//    		}
    	}
    	////system.out.println(orderedObjectsFirst);
    	////system.out.println("corrobs1st" + orderedObjectsFirst);
    	return orderedObjectsFirst;
    }
    
    private ArrayList<RavensObject> putOrderedObjectsSecond(ArrayList<RavensObject> correspondingObjectsSecond){
    	ArrayList<RavensObject> orderedObjectsSecond = new ArrayList<RavensObject>();
    	
    	////system.out.println("corrobs2nd"+correspondingObjectsSecond.toString());
    	for (int i = 1; i <= correspondingObjectsSecond.size(); i+=2){
//    		if (correspondingObjectsSecond.get(i).getName().equals("deleted")){
//    			////system.out.println("deleted2ndcorrobs");
//    		}
//    		else{
    			orderedObjectsSecond.add(correspondingObjectsSecond.get(i));
//    		}
    	}
    	////system.out.println(orderedObjectsSecond);
    	////system.out.println("orderedobs2nd" + orderedObjectsSecond);
    	return orderedObjectsSecond;
    }
    
    private ArrayList<String> getAllObjectAttributes(ArrayList<RavensObject> orderedObjects){
    	ArrayList<String> allObjectAttributes = new ArrayList<String>();
    	ArrayList<String> objectAttributes = new ArrayList<String>();
    	String singleAttr = null;
    	
    	for (int i = 0; i < orderedObjects.size(); i++){
    		objectAttributes = getAttributes(orderedObjects.get(i));
    		allObjectAttributes.addAll(objectAttributes);
/*        	for (int j = 0; j<objectAttributes.size(); j++){
        		singleAttr = objectAttributes.get(j);
        		allObjectAttributes.add(singleAttr);
     */   	
    	}
    	
    	////system.out.println("obj. attributes"+allObjectAttributes);
    	return allObjectAttributes;
    	
    }
    
    private String tryAllProposedSolns(ArrayList<String> solnShoulds, ArrayList<RavensObject> figureCObjects, ArrayList<RavensObject> figure1Objects,
    		ArrayList<RavensObject> figure2Objects, ArrayList<RavensObject> figure3Objects, ArrayList<RavensObject> figure4Objects,
    		ArrayList<RavensObject> figure5Objects, ArrayList<RavensObject> figure6Objects){
    	
    	String answer = "none";
    	
    	//system.out.println(solnShoulds);
    	
    	ArrayList<RavensObject> correspondingObjectsC1 = getCorrespondingObjectsAB(figureCObjects, figure1Objects);
    	ArrayList<RavensObject> correspondingObjectsC2 = getCorrespondingObjectsAB(figureCObjects, figure2Objects);
    	ArrayList<RavensObject> correspondingObjectsC3 = getCorrespondingObjectsAB(figureCObjects, figure3Objects);
    	ArrayList<RavensObject> correspondingObjectsC4 = getCorrespondingObjectsAB(figureCObjects, figure4Objects);
    	ArrayList<RavensObject> correspondingObjectsC5 = getCorrespondingObjectsAB(figureCObjects, figure5Objects);
    	ArrayList<RavensObject> correspondingObjectsC6 = getCorrespondingObjectsAB(figureCObjects, figure6Objects);
    	
    	ArrayList<String> attrfigure1 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC1));
    	ArrayList<String> attrfigure2 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC2));
    	ArrayList<String> attrfigure3 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC3));
    	ArrayList<String> attrfigure4 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC4));
    	ArrayList<String> attrfigure5 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC5));
    	ArrayList<String> attrfigure6 = getAllObjectAttributes(putOrderedObjectsSecond(correspondingObjectsC6));
    	
    	////system.out.println("corrC1"+correspondingObjectsC1);
    	////system.out.println("corrC2"+correspondingObjectsC2);
    	////system.out.println("corrC3"+correspondingObjectsC3);
    	////system.out.println("corrC4"+correspondingObjectsC4);
    	////system.out.println("corrC5"+correspondingObjectsC5);
    	////system.out.println("corrC6"+correspondingObjectsC6);
    	
    	////system.out.println("figure1"+attrfigure1);
    	////system.out.println("figure2"+attrfigure2);
    	////system.out.println("figure3"+attrfigure3);
    	////system.out.println("figure4"+attrfigure4);
    	////system.out.println("figure5"+attrfigure5);
    	////system.out.println("figure6"+attrfigure6);
    	
    	Integer comparison1 = compareSolutionShoulds(solnShoulds, attrfigure1);
    	Integer comparison2 = compareSolutionShoulds(solnShoulds, attrfigure2);
    	Integer comparison3 = compareSolutionShoulds(solnShoulds, attrfigure3);
    	Integer comparison4 = compareSolutionShoulds(solnShoulds, attrfigure4);
    	Integer comparison5 = compareSolutionShoulds(solnShoulds, attrfigure5);
    	Integer comparison6 = compareSolutionShoulds(solnShoulds, attrfigure6);
/*    	System.out.println("attrfigureC1"+attrfigure1);
    	System.out.println("attrfigureC2"+attrfigure2);
    	System.out.println("attrfigureC3"+attrfigure3);
    	System.out.println("attrfigureC4"+attrfigure4);
    	System.out.println("attrfigureC5"+attrfigure5);
    	System.out.println("attrfigureC6"+attrfigure6);*/
    	ArrayList<Integer> compares = new ArrayList<Integer>();
    	compares.add(comparison1);
    	compares.add(comparison2);
    	compares.add(comparison3);
    	compares.add(comparison4);
    	compares.add(comparison5);
    	compares.add(comparison6);
    	
    	/*Integer solnShouldsSize = solnShoulds.size();
    	////system.out.println(solnShouldsSize);
    	
    	if (comparison1 == solnShouldsSize){
    		answer = "1";
    	}
    	else if (comparison2 == solnShouldsSize){
    		answer = "2";
    	}
    	else if (comparison3 == solnShouldsSize){
    		answer = "3";
    	}
    	else if (comparison4 == solnShouldsSize){
    		answer = "4";
    	}
    	else if (comparison5 == solnShouldsSize){
    		answer = "5";
    	}
    	else if (comparison6 == solnShouldsSize){
    		answer = "6";
    	}
    	else{*/
    		////system.out.println("Solution not found, next best?");
    		////system.out.println(compares);
    		////system.out.println(Collections.max(compares));
    		if (comparison1 == Collections.max(compares)){
    			answer = "1";
    		}
    		else if (comparison2 == Collections.max(compares)){
    			answer = "2";
    		}
    		else if (comparison3 == Collections.max(compares)){
    			answer = "3";
    		}
    		else if (comparison4 == Collections.max(compares)){
    			answer = "4";
    		}
    		else if (comparison5 == Collections.max(compares)){
    			answer = "5";
    		}
    		else if (comparison6 == Collections.max(compares)){
    			answer = "6";
    		}
    	
    		System.out.println(compares);
    	return answer;
    	
    }
    
    
    private Integer compareSolutionShoulds(ArrayList<String> solnShoulds, ArrayList<String> solnAttrs){
    	Integer solution = 0;
    	
    	////system.out.println("solnShoulds" + solnShoulds);
    	////system.out.println("SolnAttr" + solnAttrs);
    	////system.out.println("true or false" + (solnShoulds.get(0).equals(solnAttrs.get(0)) && solnShoulds.get(1).equals(solnAttrs.get(1))));
    	//A better solution for a triangle that can either be rotated or reflected would be reflection
/*    	for (int k = 0; k < solnShoulds.size(); k+=8){
    		if (solnShoulds.get(k).equals("triangle") && solnShoulds.get(k+4)){
    			
    		}
    	}
    	*/
/*    	for (int i = 0; i < solnShoulds.size(); i++){
    		if (solnShoulds.get(i).equals("deleted")){
    			solnShoulds.remove(i);
    		}
    	}*/
    	////system.out.println(solnShoulds);
    	////system.out.println(solnAttrs);
    	
    	if (solnShoulds.size()>0){
	    	if (solnShoulds.get(0).equals(solnAttrs.get(0))){
	    		solution++;
	    	}
    	}
    	////system.out.println("I got here");
    	if (solnShoulds.size() < solnAttrs.size()){
    		solution-=3;
    	}
    	    	
    	else if (solnShoulds.size() > solnAttrs.size()){
    		solution-=3;
	    	for (int j = 0; j < solnAttrs.size(); j += 8){
	    		if (solnShoulds.get(j).equals(solnAttrs.get(j))){
	    			solution++;
	    		}
	    	}
	    	for (int k = 0; k < solnAttrs.size(); k += 8){
	    		if (solnShoulds.get(k).equals(solnAttrs.get(k))){
	    			solution++;
	    		}
	    	}
	    	for (int k = 1; k < solnAttrs.size(); k += 8){
	    		if (solnShoulds.get(k).equals(solnAttrs.get(k))){
	    			solution++;
	    		}
	    	}
	    	for (int k = 2; k < solnAttrs.size(); k += 8){
	    		if (solnShoulds.get(k).equals(solnAttrs.get(k))){
	    			solution++;
	    		}
	    	}
	    	for (int i = 0; i < solnAttrs.size(); i++){
	    		if (solnShoulds.get(i).equals(solnAttrs.get(i))){
	    			solution++;
	    		}
	    	}
	    	for (int l = 0; l < solnAttrs.size(); l++){
	    		if (!solnShoulds.get(l).equals("not here") && solnAttrs.get(l).equals("not here")){
	    			solution--;
	    		}
	    		else if (solnShoulds.get(l).equals("not here") && !solnAttrs.get(l).equals("not here")){
	    			solution--;
	    		}
	    	}
    	}
    	else{
	    	//TODO bump better attrs
	    	for (int j = 0; j < solnShoulds.size(); j += 8){
	    		if (solnShoulds.get(j).equals(solnAttrs.get(j))){
	    			solution++;
	    		}
	    	}
	    	for (int k = 0; k < solnAttrs.size(); k += 8){
	    		if (solnShoulds.get(k).equals(solnAttrs.get(k))){
	    			solution++;
	    		}
	    	}
	    	for (int k = 1; k < solnAttrs.size(); k += 8){
	    		if (solnShoulds.get(k).equals(solnAttrs.get(k))){
	    			solution++;
	    		}
	    	}
	    	for (int k = 2; k < solnShoulds.size(); k += 8){
	    		if (solnShoulds.get(k).equals(solnAttrs.get(k))){
	    			solution++;
	    		}
	    	}
	    	for (int i = 0; i < solnShoulds.size(); i++){
	    		if (solnShoulds.get(i).equals(solnAttrs.get(i))){
	    			solution++;
	    		}
	    	}
	    	for (int l = 0; l < solnShoulds.size(); l++){
	    		if (!solnShoulds.get(l).equals("not here") && solnAttrs.get(l).equals("not here")){
	    			solution--;
	    		}
	    		else if (solnShoulds.get(l).equals("not here") && !solnAttrs.get(l).equals("not here")){
	    			solution--;
	    		}
	    	}
    	}    	
    	
    	////system.out.println(solnShoulds);
    	////system.out.println(solnAttrs);
/*    	if (solnShoulds.get(0).equals(solnAttrs.get(0)) && solnShoulds.get(1).equals(solnAttrs.get(1)) && 
    		solnShoulds.get(2).equals(solnAttrs.get(2)) && solnShoulds.get(3).equals(solnAttrs.get(3)) && 
    		solnShoulds.get(4).equals(solnAttrs.get(4)) && solnShoulds.get(5).equals(solnAttrs.get(5)) && 
    		solnShoulds.get(6).equals(solnAttrs.get(6)) && solnShoulds.get(7).equals(solnAttrs.get(7))){
    		solution = 8;
    	}
    	else {
    			if (solnShoulds.get(0).equals(solnAttrs.get(0))){
    				solution++;
    			}
    			if (solnShoulds.get(1).equals(solnAttrs.get(1))){
    				solution++;
    			}
    			if (solnShoulds.get(2).equals(solnAttrs.get(2))){
    				solution++;
    			}
    			if (solnShoulds.get(3).equals(solnAttrs.get(3))){
    				solution++;
    			}
    			if (solnShoulds.get(4).equals(solnAttrs.get(4))){
    				solution++;
    			}
    			if (solnShoulds.get(5).equals(solnAttrs.get(5))){
    				solution++;
    			}
    			if (solnShoulds.get(6).equals(solnAttrs.get(6))){
    				solution++;
    			}
    			if (solnShoulds.get(7).equals(solnAttrs.get(7))){
    				solution++;
    			}
    	}*/
     	
    	return solution;
    }
    
    private ArrayList<String> solnShouldAttrs(ArrayList<String> cAttrs, ArrayList<String> bAttrs, ArrayList<String> comparisonAttrs){
    	ArrayList<String> solutionShoulds = new ArrayList<String>();
    	
    	//System.out.println("cAttrs"+cAttrs);
    	//System.out.println("bAttrs"+bAttrs);
    	//System.out.println("comparisonAttrs"+comparisonAttrs);
    	
    	if (cAttrs.size() >= comparisonAttrs.size()){
	    	for (int i = 0; i < comparisonAttrs.size(); i++){
	    		////system.out.println("i"+i+" compattr "+comparisonAttrs.get(i));
	    		if (comparisonAttrs.get(i).equals("unchanged")){
	    			solutionShoulds.add(cAttrs.get(i));
	    		}
		    	else if ((i == 4 || i == 13 || i == 22) && !comparisonAttrs.get(i).equals("not here") && !comparisonAttrs.get(i).equals("deleted")
		    			&& !comparisonAttrs.get(i).equals("no item")){
		    		if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
		    		}
		    		else{
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
		    		}
/*		    		if (cAttrs.get(i-4).equals("triangle") && comparisonAttrs.get(i).equals("180")){
		    			solutionShoulds.set(i,"0");
		    		}*/
		    	}
		    	else if ((i==1 || i==10 || i==19) && ((comparisonAttrs.get(i).contains("top-left")) ||
		    			comparisonAttrs.get(i).contains("top-right") || comparisonAttrs.get(i).contains("bottom-right")
		    			|| comparisonAttrs.get(i).contains("bottom-left"))){
		    		
		    		String[] values = comparisonAttrs.get(i).split("[,]");
		    		String[] cValues = cAttrs.get(i).split("[,]");
		    		String solnShouldFill="";
		    		
		    		for (int j = 0; j < cValues.length; j++){
		    			
		    			////system.out.println(cValues[j]);
		    			
		    			if (solnShouldFill.contains(cValues[j])){
		    				
		    			}
		    			else {
		    				if (!solnShouldFill.isEmpty()){
		    					solnShouldFill += ",";
		    				}
		    				solnShouldFill += cValues[j];
		    			}
		    		}
		    		
		    		for (int j = 0; j < values.length; j++){
		    			if (solnShouldFill.contains(values[j])){
		    				
		    			}
		    			else {
		    				if (!solnShouldFill.isEmpty()){
		    					solnShouldFill += ",";
		    				}
		    				solnShouldFill += values[j];
		    			}
		    		}
		    		////system.out.println(solnShouldFill);
		    		solutionShoulds.add(solnShouldFill);
		    	}
		    	else if (comparisonAttrs.get(i).equals("deleted")){
		    		////system.out.println("I got here");
		    		solutionShoulds.add("deleted");
		    	}
		    	else if (comparisonAttrs.get(i).equals("no item")){
		    		 solutionShoulds.add(cAttrs.get(i));
		    		 solutionShoulds.add(bAttrs.get(i));
		    	}
		    	
		    	else {
		    		solutionShoulds.add(comparisonAttrs.get(i));
		    	}
		    	////system.out.println(solutionShoulds);
		    }
    	}
    	
    	else if (cAttrs.size() < comparisonAttrs.size()){
	    	for (int i = 0; i < cAttrs.size(); i++){
	    		if (comparisonAttrs.get(i).equals("unchanged") || comparisonAttrs.get(i).equals("no item")){
	    			solutionShoulds.add(cAttrs.get(i));
	    		}
		    	else if (i == 4 && !comparisonAttrs.get(i).equals("not here") && !comparisonAttrs.get(i).equals("deleted")&& !comparisonAttrs.get(i).equals("no item")){
		    		if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
		    		}
		    		else{
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
		    		}
		    	}
		    	else if (i == 13 && !comparisonAttrs.get(i).equals("not here") && !comparisonAttrs.get(i).equals("deleted")&& !comparisonAttrs.get(i).equals("no item")){
		    		if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
		    		}
		    		else{
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
		    		}
		    	}
		    	else if (i == 22 && !comparisonAttrs.get(i).equals("not here") && !comparisonAttrs.get(i).equals("deleted")&& !comparisonAttrs.get(i).equals("no item")){
		    		if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
		    		}
		    		else{
		    			solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
		    		}
		    	}
		    	else if (comparisonAttrs.get(i).equals("deleted")){
		    		solutionShoulds.add("deleted");
		    	}
		    	else {
		    		solutionShoulds.add(comparisonAttrs.get(i));
		    	}
		    	
		    }
	    	int nextValue = 0;
	    	if (comparisonAttrs.get(0).equals("not here")){
	    		nextValue = solutionShoulds.size();
	    	}
	    	else{
	    		nextValue = 0;
	    	}
	    	//System.out.println(nextValue);
	    	//System.out.println(comparisonAttrs.size());
	    	for (int j = nextValue; j < bAttrs.size(); j++){
	    		solutionShoulds.add(bAttrs.get(j));
	    	}
    	}
    	
    	if (cAttrs.size() > comparisonAttrs.size()){
    		
    		int nextValue = solutionShoulds.size();
    		
    		for (int k = nextValue; k < cAttrs.size(); k++){
    			////system.out.println("I got here");
/*    			if (comparisonAttrs.get(k).equals("deleted")){
    				solutionShoulds.add("deleted");
    			}
    			else{*/
    				solutionShoulds.add(solutionShoulds.get(k-9));
    			//}
    		}
    	}
    	
    	for (int k = 0; k < solutionShoulds.size(); k+=8){
    		if (solutionShoulds.get(k).equals("triangle") && solutionShoulds.get(k+4).equals("180")){
    			solutionShoulds.remove(k+8);
    			solutionShoulds.add(k+8,"yes");
    		}
    	}
    	
    	/*else if (cAttrs.size() < comparisonAttrs.size()) {
    		
    		for (int i = 0; i < cAttrs.size(); i++){
    			if (comparisonAttrs.get(i).equals("unchanged")){
    				solutionShoulds.add(cAttrs.get(i));
    			}
    			
	    		else if (i == 4 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
	    			}
	    		}
	    		else if (i == 13 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
	    			}
	    		}
	    		else if (i == 22 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
	    			}
	    		}
	    		else {
	    			solutionShoulds.add(comparisonAttrs.get(i));
	    		}
    		}
	    
    		int nextVal = solutionShoulds.size();
    		
    		for (int i = nextVal; i < (comparisonAttrs.size()); i++){
    			if (comparisonAttrs.get(i).equals("unchanged")){
    				solutionShoulds.add(bAttrs.get(i));
    			}
	    		else if (i == 4 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i))-360));
	    			}
	    		}
	    		else if (i == 13 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i))-360));
	    			}
	    		}
	    		else if (i == 22 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(bAttrs.get(i))-360));
	    			}
	    		}
	    		else {
	    			solutionShoulds.add(comparisonAttrs.get(i));
	    		}
	    		
	    	}
    	}
    	else if (comparisonAttrs.size() < cAttrs.size()){
    		//system.out.println("I got here");
    		for (int i = 0; i < cAttrs.size(); i++){
    			if (comparisonAttrs.get(i).equals("unchanged")){
    				solutionShoulds.add(cAttrs.get(i));
    			}
    			
	    		else if (i == 4 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
	    			}
	    		}
	    		else if (i == 13 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
	    			}
	    		}
	    		else if (i == 22 && !comparisonAttrs.get(i).equals("not here")){
	    			if (Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i)) < 360){
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))));
	    			}
	    			else{
	    				solutionShoulds.add(Integer.toString(Integer.parseInt(comparisonAttrs.get(i))+ Integer.parseInt(cAttrs.get(i))-360));
	    			}
	    		}
	    		else {
	    			solutionShoulds.add(comparisonAttrs.get(i));
	    		}
    		}
    		
    		int nextValue = solutionShoulds.size();
    		
    		for (int j = nextValue; j < cAttrs.size(); j++){
    			solutionShoulds.add(solutionShoulds.get(j-9));
    		}
    	}
    	
	    		
	  */
	    	
    	//system.out.println("solnShoulds"+solutionShoulds);
    	/*int nextVal = solutionShoulds.size();
    	
    	if (cAttrs.size() > comparisonAttrs.size()){
    		for (int j = nextVal; j < cAttrs.size(); j++){
    			solutionShoulds.add(solutionShoulds.get(j-9));
    		}
    	}*/
    	
    	////system.out.println(solutionShoulds);
	    ////system.out.println(solutionShoulds);
    	return solutionShoulds;
    }
    
    private ArrayList<String> compareAttributesAB(ArrayList<String> object1Attr, ArrayList<String> object2Attr){
    	ArrayList<String> changes = new ArrayList<String>();
    	
    	//System.out.println("object1Attr"+object1Attr);
    	//System.out.println("object2Attr"+object2Attr);

    	for (int i = 0; i < object1Attr.size(); i++){
/*    		if ((i==4 || i==13 || i== 22) && !object1Attr.get(i).equals("0") && !object1Attr.get(i).equals("deleted")
    				&& !object1Attr.get(i).equals("not here")){
    			//System.out.println(Integer.toString((Integer.parseInt(object2Attr.get(i))- Integer.parseInt(object1Attr.get(i)))));
    			object2Attr.set(i, Integer.toString((Integer.parseInt(object2Attr.get(i))- Integer.parseInt(object1Attr.get(i)))));
    			object1Attr.set(i, "0");
    			//System.out.println(object2Attr);
    			//System.out.println(object1Attr);
    		}*/
    		if (!(object1Attr.get(i)).equals((object2Attr.get(i))) && (i !=4 && i !=13 && i!=22) && !object1Attr.get(i).equals("no item")){
    			changes.add(object2Attr.get(i));
    		}
    		
    		else if (object1Attr.get(i).equals("not here")&& !object1Attr.get(i).equals("no item") /*&& object2Attr.get(i).equals("not here")*/){
    			changes.add("not here");
    		}		
    		else if ((i == 4 || i == 13 || i == 22) && !(object1Attr.get(i).equals((object2Attr.get(i)))) &&
    				!object1Attr.get(i).equals("not here") && !object2Attr.get(i).equals("deleted") && !object1Attr.get(i).equals("no item")
    			){
    			if (Integer.parseInt(object1Attr.get(i))+Integer.parseInt(object2Attr.get(i)) < 360){
    				changes.add(Integer.toString((Integer.parseInt(object1Attr.get(i)) + Integer.parseInt(object2Attr.get(i)))));
    			}
    			else{
    				changes.add(Integer.toString((Integer.parseInt(object1Attr.get(i))+Integer.parseInt(object2Attr.get(i)))-360));
    			}
    		} 
    		else if (!(object1Attr.get(i)).equals((object2Attr.get(i))) && (i !=4 && i !=13 && i!=22)&& !object1Attr.get(i).equals("no item")){
    			changes.add(object2Attr.get(i));
    		}
    		else if (object2Attr.get(i).equals("deleted")&& !object1Attr.get(i).equals("no item")){
    			changes.add("deleted");
    		}
    		else if (object1Attr.get(i).equals("no item")){
    			changes.add("no item");
    		}
    		else{
    			changes.add("unchanged");
    		}
   		
    	}
/*    	System.out.println("lastindexof"+changes.lastIndexOf("no item"));
    	for (int j = 0; j < object1Attr.size(); j++){
    		if (changes.size() <= changes.lastIndexOf("no item")+1 && object1Attr.get(j).equals("no item")){
    			System.out.println("I got here");
    			changes.add(object2Attr.get(j));
    			System.out.println("I got here");
    		}
    		else if (object1Attr.get(j).equals("no item") && changes.size() > changes.lastIndexOf("no item")+1){
    			changes.add((changes.lastIndexOf("no item")+1+j), object2Attr.get(j));
    		}
    	}*/
    	int indexOf = changes.indexOf("no item");
    	//System.out.println("compareAttrAB"+changes);
    	//System.out.println(indexOf+10);
    	ArrayList<String> changesHelper = new ArrayList<String>();
    	for (int j = 0; j<changes.size(); j+=9){
    		if (indexOf >= 0 && (indexOf+10>changes.size())){
    			changesHelper.add(object2Attr.get(j));
    			changesHelper.add(object2Attr.get(j+1));
    			changesHelper.add(object2Attr.get(j+2));
    			changesHelper.add(object2Attr.get(j+3));
    			changesHelper.add(object2Attr.get(j+4));
    			changesHelper.add(object2Attr.get(j+5));
    			changesHelper.add(object2Attr.get(j+6));
    			changesHelper.add(object2Attr.get(j+7));
    			changesHelper.add(object2Attr.get(j+8));
    		}
    		else if (indexOf >= 0 && (indexOf+10<=changes.size())){
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+1));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+2));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+3));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+4));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+5));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+6));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+7));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+8));
    		}
    	}
    	//System.out.println(changesHelper);
    	
    	//System.out.println("compareAttrAB"+changes);
    	////system.out.println(changes);
    	return changes;
    }
    
    private ArrayList<String> compareAttributesAB2x2(ArrayList<String> object1Attr, ArrayList<String> object2Attr){
    	ArrayList<String> changes = new ArrayList<String>();
    	
    	//System.out.println("object1Attr"+object1Attr);
    	//System.out.println("object2Attr"+object2Attr);

    	for (int i = 0; i < object1Attr.size(); i++){
/*    		if ((i==4 || i==13 || i== 22) && !object1Attr.get(i).equals("0") && !object1Attr.get(i).equals("deleted")
    				&& !object1Attr.get(i).equals("not here")){
    			//System.out.println(Integer.toString((Integer.parseInt(object2Attr.get(i))- Integer.parseInt(object1Attr.get(i)))));
    			object2Attr.set(i, Integer.toString((Integer.parseInt(object2Attr.get(i))- Integer.parseInt(object1Attr.get(i)))));
    			object1Attr.set(i, "0");
    			//System.out.println(object2Attr);
    			//System.out.println(object1Attr);
    		}*/
    		if (!(object1Attr.get(i)).equals((object2Attr.get(i))) && (i !=4 && i !=13 && i!=22) && !object1Attr.get(i).equals("no item")){
    			changes.add(object2Attr.get(i));
    		}
    		
    		else if (object1Attr.get(i).equals("not here")&& !object1Attr.get(i).equals("no item") /*&& object2Attr.get(i).equals("not here")*/){
    			changes.add("not here");
    		}		
    		else if ((i == 4 || i == 13 || i == 22) && !(object1Attr.get(i).equals((object2Attr.get(i)))) &&
    				!object1Attr.get(i).equals("not here") && !object2Attr.get(i).equals("deleted") && !object1Attr.get(i).equals("no item")
    			){
    			if (Integer.parseInt(object1Attr.get(i))-Integer.parseInt(object2Attr.get(i)) < 360){
    				changes.add(Integer.toString((Integer.parseInt(object1Attr.get(i)) - Integer.parseInt(object2Attr.get(i)))));
    			}
    			else{
    				changes.add(Integer.toString((Integer.parseInt(object1Attr.get(i))-Integer.parseInt(object2Attr.get(i)))-360));
    			}
    		} 
    		else if (!(object1Attr.get(i)).equals((object2Attr.get(i))) && (i !=4 && i !=13 && i!=22)&& !object1Attr.get(i).equals("no item")){
    			changes.add(object2Attr.get(i));
    		}
    		else if (object2Attr.get(i).equals("deleted")&& !object1Attr.get(i).equals("no item")){
    			changes.add("deleted");
    		}
    		else if (object1Attr.get(i).equals("no item")){
    			changes.add(object2Attr.get(i));
    		}
    		else{
    			changes.add("unchanged");
    		}
   		
    	}
/*    	System.out.println("lastindexof"+changes.lastIndexOf("no item"));
    	for (int j = 0; j < object1Attr.size(); j++){
    		if (changes.size() <= changes.lastIndexOf("no item")+1 && object1Attr.get(j).equals("no item")){
    			System.out.println("I got here");
    			changes.add(object2Attr.get(j));
    			System.out.println("I got here");
    		}
    		else if (object1Attr.get(j).equals("no item") && changes.size() > changes.lastIndexOf("no item")+1){
    			changes.add((changes.lastIndexOf("no item")+1+j), object2Attr.get(j));
    		}
    	}*/
    	int indexOf = changes.indexOf("no item");
    	//System.out.println("compareAttrAB"+changes);
    	//System.out.println(indexOf+10);
    	ArrayList<String> changesHelper = new ArrayList<String>();
    	for (int j = 0; j<changes.size(); j+=9){
    		if (indexOf >= 0 && (indexOf+10>changes.size())){
    			changesHelper.add(object2Attr.get(j));
    			changesHelper.add(object2Attr.get(j+1));
    			changesHelper.add(object2Attr.get(j+2));
    			changesHelper.add(object2Attr.get(j+3));
    			changesHelper.add(object2Attr.get(j+4));
    			changesHelper.add(object2Attr.get(j+5));
    			changesHelper.add(object2Attr.get(j+6));
    			changesHelper.add(object2Attr.get(j+7));
    			changesHelper.add(object2Attr.get(j+8));
    		}
    		else if (indexOf >= 0 && (indexOf+10<=changes.size())){
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+1));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+2));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+3));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+4));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+5));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+6));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+7));
    			changesHelper.add(changes.lastIndexOf("no item")+1, object2Attr.get(j+8));
    		}
    	}
    	//System.out.println(changesHelper);
    	
    	//System.out.println("compareAttrAB"+changes);
    	////system.out.println(changes);
    	return changes;
    }
    
    private ArrayList<String> compareAttributesAC(ArrayList<String> object1Attr, ArrayList<String> object2Attr){
    	ArrayList<String> changes = new ArrayList<String>();
    	
    	////system.out.println(object1Attr);
    	////system.out.println(object2Attr);
    	
    	for (int i = 0; i < object1Attr.size(); i++){
    		if (!(object1Attr.get(i)).equals((object2Attr.get(i))) && object1Attr.get(i).equals("not here")){
    			changes.add("not here");
    		}
    		else if (!(object1Attr.get(i).equals((object2Attr.get(i))))){
    			changes.add(object2Attr.get(i));
    		}
    		else{
    			changes.add("unchanged");
    		}
    	}
    	
    	////system.out.println(changes);
    	return changes;
    }
    
    private ArrayList<String> getAttributes(RavensObject object1){
    	//Attributes will just be held at different indices of the ArrayList:
    	//[0] shape
    	//[1] fill
    	//[2] size 
    	//[3] inside
    	//[4] angle
    	//[5] above
    	//[6] left-of
    	//[7] vertical-flip
    	//[8] overlaps
    	ArrayList<String> attributes = new ArrayList<String>();
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	attributes.add("not here");
    	
    	//TODO figure out placement attributes and angle attributes
    	
    	for (int i = 0; i < object1.getAttributes().size(); i++){
    		if (object1.getAttributes().get(i).getName().equals("shape")){
    			attributes.remove(0);
    			attributes.add(0,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("fill")){
    			attributes.remove(1);
    			attributes.add(1,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("size")){
    			attributes.remove(2);
    			attributes.add(2,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("inside")){
    			attributes.remove(3);
    			attributes.add(3,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("angle")){
    			attributes.remove(4);
    			//TODO FIX THIS
    			if (object1.getAttributes().get(0).getValue().equals("plus")){
    				attributes.add(4,object1.getAttributes().get(i).getValue());
    			}
    			else{
    				attributes.add(4,object1.getAttributes().get(i).getValue());
    			}
    		}
    		if (object1.getAttributes().get(i).getName().equals("above")){
    			attributes.remove(5);
    			attributes.add(5,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("left-of")){
    			attributes.remove(6);
    			attributes.add(6,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("vertical-flip")){
    			attributes.remove(7);
    			attributes.add(7,object1.getAttributes().get(i).getValue());
    		}
    		if (object1.getAttributes().get(i).getName().equals("overlaps")){
    			attributes.remove(8);
    			attributes.add(8,object1.getAttributes().get(i).getValue());
    		}
    	}
    	
    	if (object1.getName().equals("deleted")){
    		////system.out.println("I got here");
    		for (int j = 0; j < 9; j++){
    			attributes.remove(j);
    			attributes.add(j,"deleted");
    		}
    	}
    	
    	if (object1.getName().equals("no item")){
    		for (int k = 0; k < 9; k++){
    			attributes.remove(k);
    			attributes.add(k,"no item");
    		}
    	}
    	
    	////system.out.println(attributes);
    	return attributes;
    }
    
    private Integer attributesScorerAC(ArrayList<String> attribute1, ArrayList<String> attribute2){
    	Integer score = 0;
    	
    	////system.out.println(attribute1.toString());
    	////system.out.println(attribute2.toString());
    	/*if (attribute1.get(1).equals(attribute2.get(1))){
    		score += 2;
    	}*/
    	for (int j = 1; j < attribute1.size(); j+=9){
    		if (attribute1.get(j).equals(attribute2.get(j))){
    			score++;
    		}
    	}
    	
    	for (int i = 1; i < attribute1.size(); i++){
    		if (attribute1.get(i).equals(attribute2.get(i))){
    			score++;
    		}
    	}
    	
    	////system.out.println(score);
    	
    	return score*3;
    }
    
    private ArrayList<RavensObject> getCorrespondingObjectsAC(ArrayList<RavensObject> figureAObjects, ArrayList<RavensObject> figureBObjects){
    	
    	ArrayList<RavensObject> correspondingObjectsAB = new ArrayList<RavensObject>();
    	ArrayList<Integer> attributeScore = new ArrayList<Integer>();
    	RavensObject deleted = new RavensObject("deleted");
    	RavensObject noItem = new RavensObject("no item");
    	
    	if (figureAObjects.size() == 1 && figureBObjects.size() == 1){
    		correspondingObjectsAB.add(figureAObjects.get(0));
    		correspondingObjectsAB.add(figureBObjects.get(0));
    	}
    	else{
    		for (int i = 0; i < figureAObjects.size(); i++){
    			for (int j = 0; j < figureBObjects.size(); j++){
    				attributeScore.add(i);
    				attributeScore.add(j);
    				attributeScore.add(attributesScorerAC(getAttributes(figureAObjects.get(i)), getAttributes(figureBObjects.get(j))));
    			}
    		}
    		////system.out.println(attributeScore);
    		while (attributeScore.size() > 0){
    			int maxIndex = attributeScore.indexOf(Collections.max(attributeScore));
    			if (correspondingObjectsAB.contains(figureAObjects.get(attributeScore.get(maxIndex-2))) ||
    					correspondingObjectsAB.contains(figureBObjects.get(attributeScore.get(maxIndex-1)))){
    				attributeScore.remove(maxIndex);
    				attributeScore.remove(maxIndex-1);
    				attributeScore.remove(maxIndex-2);
    			}
    			else{
    				correspondingObjectsAB.add(figureAObjects.get(attributeScore.get(maxIndex-2)));
    				correspondingObjectsAB.add(figureBObjects.get(attributeScore.get(maxIndex-1)));
    				attributeScore.remove(maxIndex);
    				attributeScore.remove(maxIndex-1);
    				attributeScore.remove(maxIndex-2);
    				
    			}
    			////system.out.println(attributeScore);
    		}
			////system.out.println(correspondingObjectsAB);
    		for (int k = 0; k < figureAObjects.size(); k++){
    			for (int l = 0; l < figureBObjects.size(); l++){
    				if (correspondingObjectsAB.contains(figureAObjects.get(k))){
    					
    				}
    				else{
    					correspondingObjectsAB.add(figureAObjects.get(k));
    					correspondingObjectsAB.add(deleted);
    				}
    			}
    		}
    	}
    	
    	if (figureAObjects.size() < figureBObjects.size() && figureAObjects.size() != 0){
    		int nextVal = correspondingObjectsAB.size()/2;
    		
    		for (int m = nextVal; m < figureBObjects.size(); m++){
    			correspondingObjectsAB.add(figureAObjects.get(0));
    			correspondingObjectsAB.add(figureBObjects.get(m));
    		}
    	}
    	
		if (figureAObjects.size() < figureBObjects.size() && figureAObjects.size() == 0){
			int nextVal = correspondingObjectsAB.size()/2;
			
			for (int m = nextVal; m < figureBObjects.size(); m++){
				correspondingObjectsAB.add(noItem);
				correspondingObjectsAB.add(figureBObjects.get(m));
			}
		}

    	////system.out.println("attrScore" + attributeScore);
    	////system.out.println(correspondingObjectsAB);
    	
    	return correspondingObjectsAB;
    	
    }
    
    private Integer attributesScorerAB(ArrayList<String> attribute1, ArrayList<String> attribute2){
    	Integer score = 0;
    	
    	////system.out.println(attribute1.toString());
    	////system.out.println(attribute2.toString());
    	if (attribute1.get(0).equals(attribute2.get(0))){
    		score += 3;
    	}
    	
    	
    	for (int i = 1; i < attribute1.size(); i++){
    		if (attribute1.get(i).equals(attribute2.get(i))){
    			score++;
    		}
    	}
    	
    	////system.out.println(score);
    	
    	return score*3;
    }
    
    private ArrayList<Integer> attributeScoreHelper(ArrayList<Integer> attributeScore, int index){
    	ArrayList<Integer> attributeScoreHelper = new ArrayList<Integer>();
    	
    	for (int i = 0; i < attributeScore.size(); i+=3){
    		if (attributeScore.get(i) == index){
    			attributeScoreHelper.add(attributeScore.get(i));
    			attributeScoreHelper.add(attributeScore.get(i+1));
    			attributeScoreHelper.add(attributeScore.get(i+2));
    		}
    	}
    	
    	return attributeScoreHelper;
    }
    
    private ArrayList<RavensObject> getCorrespondingObjectsAB(ArrayList<RavensObject> figureAObjects, ArrayList<RavensObject> figureBObjects){
    	
    	ArrayList<RavensObject> correspondingObjectsAB = new ArrayList<RavensObject>();
    	ArrayList<Integer> attributeScore = new ArrayList<Integer>();
    	RavensObject deleted = new RavensObject("deleted");
    	RavensObject noItem = new RavensObject("no item");
    	ArrayList<Integer> bestMatchIndices = new ArrayList<Integer>();
    	
    	if (figureAObjects.size() == 1 && figureBObjects.size() == 1){
    		correspondingObjectsAB.add(figureAObjects.get(0));
    		correspondingObjectsAB.add(figureBObjects.get(0));
    	}
    	else {
    		for (int i = 0; i < figureAObjects.size(); i++){
    			for (int j = 0; j < figureBObjects.size(); j++){
    				attributeScore.add(i);
    				attributeScore.add(j);
    				attributeScore.add(attributesScorerAB(getAttributes(figureAObjects.get(i)), getAttributes(figureBObjects.get(j)))*3);
    			}
    		}
    		
    		
    		////system.out.println(attributeScore);
    		
    		while (attributeScore.size() > 0){
    			int maxIndex = attributeScore.indexOf(Collections.max(attributeScore));
    			if (correspondingObjectsAB.contains(figureAObjects.get(attributeScore.get(maxIndex-2))) ||
    					correspondingObjectsAB.contains(figureBObjects.get(attributeScore.get(maxIndex-1)))){
    				attributeScore.remove(maxIndex);
    				attributeScore.remove(maxIndex-1);
    				attributeScore.remove(maxIndex-2);
    			}
    			else{
    				correspondingObjectsAB.add(figureAObjects.get(attributeScore.get(maxIndex-2)));
    				correspondingObjectsAB.add(figureBObjects.get(attributeScore.get(maxIndex-1)));
    				attributeScore.remove(maxIndex);
    				attributeScore.remove(maxIndex-1);
    				attributeScore.remove(maxIndex-2);
    				
    			}
    			////system.out.println(attributeScore);
    		}
    		
    		
    			
    		/*for (int i = 2; i < attributeScore.size(); i += 3){
    			ArrayList<Integer> attributeScoreHelper = new ArrayList<Integer>();
    			attributeScoreHelper.add(attributeScore.get(i-2));
    			attributeScoreHelper.add(attributeScore.get(i-1));
    			attributeScoreHelper.add(attributeScore.get(i));
    			
    			int maxIndex = attributeScoreHelper.indexOf(Collections.max(attributeScoreHelper));

    			if (correspondingObjectsAB.contains(figureAObjects.get(attributeScoreHelper.get(maxIndex-2))) ||
    					correspondingObjectsAB.contains(figureBObjects.get(attributeScoreHelper.get(maxIndex-1)))){
    				
    			}
    			else{
    				correspondingObjectsAB.add(figureAObjects.get(attributeScoreHelper.get(maxIndex-2)));
    				correspondingObjectsAB.add(figureBObjects.get(attributeScoreHelper.get(maxIndex-1)));
    				attributeScoreHelper.remove(maxIndex);
    				attributeScoreHelper.add(maxIndex, 0);
    				
    			}
    			////system.out.println("I got here"+i);		
    		}
    		
*/    		for (int k = 0; k < figureAObjects.size(); k++){
    			for (int l = 0; l < figureBObjects.size(); l++){
    				if (correspondingObjectsAB.contains(figureAObjects.get(k))){
    					
    				}
    				else{
    					correspondingObjectsAB.add(figureAObjects.get(k));
    					correspondingObjectsAB.add(deleted);
    				}
    			}
    			
    			if (correspondingObjectsAB.contains(figureAObjects.get(k))){
    				
    			}
    			else if (figureBObjects.size()==0){
    				correspondingObjectsAB.add(figureAObjects.get(k));
    				correspondingObjectsAB.add(deleted);
    			}
    		}
		
		
		if (figureAObjects.size() < figureBObjects.size()){
			int nextVal = correspondingObjectsAB.size()/2;
			
			for (int m = nextVal; m < figureBObjects.size(); m++){
				correspondingObjectsAB.add(noItem);
				correspondingObjectsAB.add(figureBObjects.get(m));
			}
		}

    		
    	}
    	/*else {
    		for (int i = 0; i < figureAObjects.size(); i++){
    			for (int j = 0; j < figureBObjects.size(); j++){
    				attributeScore.add(i);
    				attributeScore.add(j);
    				attributeScore.add(attributesScorerAB(getAttributes(figureAObjects.get(i)), getAttributes(figureBObjects.get(j))));
    			}
    		}
    		////system.out.println(attributeScore);
    		
    			////system.out.println(figureAObjects.get(attributeScore.get(i-2)));
    			////system.out.println(figureBObjects.get(attributeScore.get(i-1)));
    			
    			////system.out.println(correspondingObjectsAB.contains(figureBObjects.get(attributeScore.get(i-1))));
    			
    			if (correspondingObjectsAB.contains(figureAObjects.get(attributeScore.get(i-2))) || 
    					correspondingObjectsAB.contains(figureBObjects.get(attributeScore.get(i-1)))){
    				
    			}
    			else if (attributeScore.get(i) == Collections.max(attributeScoreHelper)){
    				correspondingObjectsAB.add(figureAObjects.get(attributeScore.get(i-2)));
    				correspondingObjectsAB.add(figureBObjects.get(attributeScore.get(i-1)));
    			}
    		}
			////system.out.println(correspondingObjectsAB);
    		for (int k = 0; k < figureAObjects.size(); k++){
    			for (int l = 0; l < figureBObjects.size(); l++){
    				if (correspondingObjectsAB.contains(figureAObjects.get(k))){
    					
    				}
    				else{
    					correspondingObjectsAB.add(figureAObjects.get(k));
    					correspondingObjectsAB.add(deleted);
    				}
    			}
    		}
    	}
    	
    	if (figureAObjects.size() < figureBObjects.size()){
    		int nextVal = correspondingObjectsAB.size()/2;
    		
    		for (int m = nextVal; m < figureBObjects.size(); m++){
    			correspondingObjectsAB.add(figureAObjects.get(0));
    			correspondingObjectsAB.add(figureBObjects.get(m));
    		}
    	}
*/
    	////system.out.println("attrScore" + attributeScore);
    	////system.out.println(correspondingObjectsAB);
    	////system.out.println("corresAB"+correspondingObjectsAB);
    	return correspondingObjectsAB;
    	
    }
}
    
    /*private ArrayList<RavensObject> getCorrespondingObjectsAC(ArrayList<RavensObject> figureAObjects, ArrayList<RavensObject> figureCObjects){
    	
    	////system.out.println("Start getCorrespondingObjectsAC");
    	ArrayList<RavensObject> correspondingObjectsAC = new ArrayList<RavensObject>();
    	Integer attrCount;
    	RavensObject deletedObject = new RavensObject("deleted");
    	////system.out.println("deletedname "+deletedObject.getName());
    	
    	//If there's only one object in both figures, they correspond automatically.
    	if (figureAObjects.size() == 1 && figureCObjects.size() == 1){
    		correspondingObjectsAC.add(figureAObjects.get(0));
    		correspondingObjectsAC.add(figureCObjects.get(0));
    	}
    	
    	//Filler code that corresponds by shape name
    	//TODO add real correspondence logic
    	////system.out.println(figureAObjects.size());
    	////system.out.println(figureCObjects.size());
    	
    	for (int i = 0; i < figureAObjects.size(); i++){
    		for (int j = 0; j < figureCObjects.size(); j++){
    			if (correspondingObjectsAC.contains(figureAObjects.get(i))){
    				
    			}
    			else if (figureAObjects.get(i).getName().equals(figureCObjects.get(j).getName())){
    				if (figureAObjects.get(i).getName().equals(figureCObjects.get(j).getName())){
    					correspondingObjectsAC.add(figureAObjects.get(i));
    					correspondingObjectsAC.add(figureCObjects.get(j));
    				}
    				else if (figureAObjects.size() > figureCObjects.size()){
    					correspondingObjectsAC.add(figureAObjects.get(i));
    					correspondingObjectsAC.add(deletedObject);
    				}
    			}
    		}
    	}
    	
    	for (int i = 0; i < figureAObjects.size(); i++){
    		if (correspondingObjectsAC.contains(figureAObjects.get(i))){
    			
    		}
    		else{
    			correspondingObjectsAC.add(figureAObjects.get(i));
    			correspondingObjectsAC.add(deletedObject);
    		}
    	}
    	
    	if (figureAObjects.size() > figureCObjects.size()){
        	for (int i = 0; i < figureAObjects.size(); i++){
        		for (int j = 0; j < figureCObjects.size(); j++){
        			if (figureAObjects.get(i).getName().equals(figureCObjects.get(j).getName())){
        			}
        			else{
        				//system.out.println("not same size");
        				correspondingObjectsAC.add(figureAObjects.get(i));
        				correspondingObjectsAC.add(deletedObject);    
        				//figureAObjects.remove(i);
        			}
    	      	}
        	}
    	}
        if (figureAObjects.size() == figureCObjects.size()){
        	//system.out.println("same size");
        	for (int k = 0; k < figureAObjects.size(); k++){
        		for (int l = 0; l < figureCObjects.size(); l++){
        			if (figureAObjects.get(k).getName().equals(figureCObjects.get(l).getName())){
        				correspondingObjectsAC.add(figureAObjects.get(k));
        				correspondingObjectsAC.add(figureCObjects.get(l));
        			}
        		}
        	}
        }
    	
    	for (int i = 0; i < figureAObjects.size(); i++){
    		for (int j = 0; j < figureCObjects.size(); j++){
    			if (figureAObjects.get(i).getName().equals(figureCObjects.get(j).getName())){
    				correspondingObjectsAC.add(figureAObjects.get(i));
    				correspondingObjectsAC.add(figureCObjects.get(j));
    			}
    			else if (figureAObjects.size() > figureCObjects.size()){
    				correspondingObjectsAC.add(figureAObjects.get(i));
    				correspondingObjectsAC.add(deletedObject);    				
    			}
    			else if (figureAObjects.size() > figureCObjects.size()){
    				correspondingObjectsAC.add(figureAObjects.get(i));
    				correspondingObjectsAC.add(deletedObject);
    			}
    			
    			////system.out.println("correspondingObjects" + figureAObjects.get(i));
    			////system.out.println("correspondingObjectsB" + figureCObjects.get(i));
    		}
    	}
    	
    	for (int k = 0; k < correspondingObjectsAC.size(); k++){
    		if (correspondingObjectsAC.get(k).equals(deletedObject)){
    			correspondingObjectsAC.remove(k);
    			correspondingObjectsAC.remove(k-1);
    		}
    	}
    	////system.out.println("correspondingObjectsAC" + correspondingObjectsAC);
		return correspondingObjectsAC;
		
    }
}
 */       
        /*ArrayList<RavensObject> correspondingObjectsAB = getCorrespondingObjectsAC(figureAObjects, figureBObjects);
        ArrayList<RavensObject> correspondingObjectsAC = getCorrespondingObjectsAC(figureAObjects, figureCObjects);
        ArrayList<RavensObject> correspondingObjectsCsol1 = getCorrespondingObjectsAC(figureAObjects, figure1Objects);
        ArrayList<RavensObject> correspondingObjectsCsol2 = getCorrespondingObjectsAC(figureAObjects, figure2Objects);
        ArrayList<RavensObject> correspondingObjectsCsol3 = getCorrespondingObjectsAC(figureAObjects, figure3Objects);
        ArrayList<RavensObject> correspondingObjectsCsol4 = getCorrespondingObjectsAC(figureAObjects, figure4Objects);
        ArrayList<RavensObject> correspondingObjectsCsol5 = getCorrespondingObjectsAC(figureAObjects, figure5Objects);
        ArrayList<RavensObject> correspondingObjectsCsol6 = getCorrespondingObjectsAC(figureAObjects, figure6Objects);
                ArrayList<RavensAttribute> figureAAttr = getAttr(figureA.getObjects());        
        ArrayList<RavensAttribute> figureBAttr = getAttr(figureB.getObjects());
        ArrayList<RavensAttribute> figureCAttr = getAttr(figureC.getObjects());
        ArrayList<RavensAttribute> figure1Attr = getAttr(figure1.getObjects());
        ArrayList<RavensAttribute> figure2Attr = getAttr(figure2.getObjects());
        ArrayList<RavensAttribute> figure3Attr = getAttr(figure3.getObjects());
        ArrayList<RavensAttribute> figure4Attr = getAttr(figure4.getObjects());
        ArrayList<RavensAttribute> figure5Attr = getAttr(figure5.getObjects());
        ArrayList<RavensAttribute> figure6Attr = getAttr(figure6.getObjects());
        
        
        
        
        for (int i = 0; i < correspondingObjects.size(); i += 2){
        	ArrayList<String> transformationsAB = getTransformation(correspondingObjects.get(i), correspondingObjects.get(i+1));
        }
        //system.out.println(correspondingObjects.get(0).getClass());
        //system.out.println(correspondingObjects.get(0));
        //system.out.println(correspondingObjects.get(1).getClass());
        //system.out.println(correspondingObjects.get(1));
        //system.out.println(correspondingObjects.get(0).getAttributes().get(0).getValue());
        //system.out.println(correspondingObjects.get(1).getAttributes().get(0).getValue());
        
        //ArrayList<String> transforms = getTransformation(correspondingObjects.get(0), correspondingObjects.get(1));
        ////system.out.println(transforms);
        
        //system.out.println("problem start");
        
        HashMap<RavensObject, ArrayList<String>> cTransformations = cTransformations(correspondingObjectsAB, correspondingObjectsAC);
        
        
        //system.out.println(figure1Attr.get(1).getValue());
        //system.out.println(cTransformations.get(correspondingObjectsAC.get(1)));
        //system.out.println(figure1Attr.get(1).getValue().equals(cTransformations.get(correspondingObjectsAC.get(1)).get(0)));
        
        
        if (attributeChecker(cTransformations, correspondingObjectsAC, figure1Attr)){
        	//system.out.println("I'm returning 1");
        	return "1";
        }
        else if (attributeChecker(cTransformations, correspondingObjectsAC, figure2Attr)){
        	//system.out.println("I'm returning 2");
        	return "2";
        }
        else if (attributeChecker(cTransformations, correspondingObjectsAC, figure3Attr)){
        	//system.out.println("I'm returning 3");
        	return "3";
        }
        else if (attributeChecker(cTransformations, correspondingObjectsAC, figure4Attr)){
        	//system.out.println("I'm returning 4");
        	return "4";
        }
        else if (attributeChecker(cTransformations, correspondingObjectsAC, figure5Attr)){
        	//system.out.println("I'm returning 5");
        	return "5";
        }
        else if (attributeChecker(cTransformations, correspondingObjectsAC, figure6Attr)){
        	//system.out.println("I'm returning 6");
        	return "6";
        }
        //system.out.println("I didn't find a match");
        return "1";
       
    }
    
    private HashSet<ArrayList<RavensObject>, ArrayList<String>> transformationAggregator(ArrayList<RavensObject> correspondingObjects){
    	ArrayList<String> transformationAggregator = new HashSet<ArrayList<RavensObject>, ArrayList<String>>;
    	
    	for (int i = 0; i < correspondingObjects.size(); i += 2){
    		ArrayList<String> transformations
    	}
    	
    }
    
    private boolean attributeChecker(HashMap<RavensObject, ArrayList<String>> cTransformations, ArrayList<RavensObject> correspondingObjectsAC, ArrayList<RavensAttribute> figure1Attr){
    	//system.out.println("Im attribute checking");
    	////system.out.println(correspondingObjectsAC.size());
    	//system.out.println(cTransformations);
    	//system.out.println(correspondingObjectsAC.get(1));
    	////system.out.println()
    	//system.out.println(correspondingObjectsAC);
    	//system.out.println(cTransformations.get((correspondingObjectsAC.get(1))));
    	  	
    	for (int j = 1; j < correspondingObjectsAC.size(); j+=2){
    		for (int i = 0; i < (cTransformations.get(correspondingObjectsAC.get(j))).size(); i++){
    			//system.out.println(cTransformations.get(correspondingObjectsAC.get(1)).get(0));
    			//system.out.println(figure1Attr.get(1).getValue());
    			for (int k = 0; k < figure1Attr.size(); k++){
    				//system.out.println("in here");
    				if (cTransformations.get(correspondingObjectsAC.get(1)).get(0).equals(figure1Attr.get(1).getValue())){
    					//system.out.println("I'm returning true");
    					return true;
    				}
    			}
    		}
    	}
    	
    	return false;
    }
    
    private HashMap<RavensObject, ArrayList<String>> cTransformations(ArrayList<RavensObject> correspondingObjectsAB, ArrayList<RavensObject> correspondingObjectsAC){
    	//system.out.println("got to cTransform");
    	HashMap<RavensObject, ArrayList<String>> transformationsAB = new HashMap<RavensObject, ArrayList<String>>();
    	HashMap<RavensObject, ArrayList<String>> transformationsAC = new HashMap<RavensObject, ArrayList<String>>();
    	RavensAttribute transAB = new RavensAttribute(null, null);
    	ArrayList<String> transformsAB;
    	
    	
    	for (int i = 0; i < correspondingObjectsAB.size(); i += 2){
    		transformsAB = getTransformation(correspondingObjectsAB.get(i), correspondingObjectsAB.get(i+1));
    		////system.out.println(correspondingObjectsAB.get(i));
    		////system.out.println(transformsAB);
    		//transformationsAB.put(correspondingObjectsAB.get(i), transformsAB);
    		for (int j = 0; j < correspondingObjectsAC.size(); j += 2){
            	////system.out.println("I got here");
            	
    			transformationsAC.put(correspondingObjectsAC.get(j+1), transformsAB);
    			////system.out.println(transformationsAC.get(correspondingObjectsAC.get(j+1)));
    		}
    	}
		
    	////system.out.println(transformationsAC);
    	
    	////system.out.println(transformationsAC.get());
    	
    	return transformationsAC;
    }
    
    private HashMap<RavensObject, ArrayList<RavensAttribute>> solObjectAttributesHolder(ArrayList<RavensObject> correspondingObjectsCSols){
    	for 
    }
    
    private ArrayList<RavensAttribute> getAttr(ArrayList<RavensObject> objects){
    	ArrayList<RavensAttribute> attributes = new ArrayList<RavensAttribute>();
    	
    	for (RavensObject objectA : objects){
    		for (RavensAttribute attr : objectA.getAttributes()){
    			attributes.add(attr);
    		}
    	}
    	return attributes;
    }
    
    private ArrayList<RavensObject> getCorrespondingObjectsAC(ArrayList<RavensObject> figureAObjects, ArrayList<RavensObject> figureCObjects){
    	
    	////system.out.println("Start getCorrespondingObjectsAC");
    	ArrayList<RavensObject> correspondingObjectsAC = new ArrayList<RavensObject>();
    	Integer attrCount;
    	RavensObject deletedObject = new RavensObject("deleted");
    	
    	//If there's only one object in both figures, they correspond automatically.
    	if (figureAObjects.size() == 1 && figureCObjects.size() == 1){
    		correspondingObjectsAC.add(figureAObjects.get(0));
    		correspondingObjectsAC.add(figureCObjects.get(0));
    		return correspondingObjectsAC;
    	}
    	
    	//this is filler code to just correspond by name
    	
    	for (int z = 0; z < figureAObjects.size(); z++){
    		for (int y = 0 ; y < figureCObjects.size(); y++){
    			if (figureAObjects.get(z).getName().equals(figureCObjects.get(y).getName())){
    				correspondingObjectsAC.add(figureAObjects.get(z));
    				correspondingObjectsAC.add(figureCObjects.get(y));
    			}
    			
    			else if (figureAObjects.size() > figureCObjects.size()){
    				if (!(figureAObjects.get(z).getName().equals(figureCObjects.get(y).getName()))){
    					correspondingObjectsAC.add(figureAObjects.get(z));
    					correspondingObjectsAC.add(deletedObject);
    				}
    			}
    		}
    	}
    	//I think if they have a similar number of attributes, they are more similar, so I will match all the ones with the same number of attributes.
    	for (int z = 0; z < figureAObjects.size(); z++){
        	ArrayList<String> correspondingObjectsACHelper  = new ArrayList<String>();
    		attrCount = 0;
    		for (int y = 0; y < figureCObjects.size(); y++){
    			//for (int i = 0; i < figureAObjects.get(z).getAttributes().size(); i++){
    				//for (int j = 0; j < figureCObjects.get(y).getAttributes().size(); j++){
    					if (figureAObjects.get(z).getAttributes().size() == figureCObjects.get(y).getAttributes().size()){
    						correspondingObjectsAC.add(figureAObjects.get(z));
    						correspondingObjectsAC.add(figureCObjects.get(y));
    					}
    				//}
    			//}
    		}
    	}
    	
    	
		return correspondingObjectsAC;
    	
    	
    }
    
    ArrayList<String> getTransformation(RavensObject objectA, RavensObject objectB){
    	//if one of the objects has the name "deleted", then make that object's transformation deleted.
    	//next, read the similar attributes and return the transformation
    	
    	//system.out.println("trans start");
    	
    	ArrayList<String> transformation = new ArrayList<String>();
    	ArrayList<RavensAttribute> objectAAttr = objectA.getAttributes();
    	ArrayList<RavensAttribute> objectBAttr = objectB.getAttributes();
    	
    	if (objectA.getName().equals("deleted") || objectB.getName().equals("deleted")){
    		transformation.add("deleted");
    		return transformation;
    	}
    	for (int j = 0; j < objectA.getAttributes().size(); j++){
    		for (int k = 0; k < objectB.getAttributes().size(); k++){
    			if (objectAAttr.get(j).getName().equals(objectBAttr.get(k).getName())){
    				if (!objectAAttr.get(j).getValue().equals(objectBAttr.get(k).getValue())){
    					transformation.add(objectBAttr.get(k).getValue());
    				}
    				if (objectAAttr.get(j).getValue().equals("no") && objectBAttr.get(k).getValue().equals("yes")){
    					transformation.add("filled");
    				}
    				else if (objectAAttr.get(j).getValue().equals("yes") && objectBAttr.get(k).getValue().equals("no")){
    					transformation.add("unfilled");
    				}
    				else if (objectAAttr.get(j).getValue().equals("small") && objectBAttr.get(k).getValue().equals("large")){
    					transformation.add("expanded");
    				}
    				else if (objectAAttr.get(j).getValue().equals("large") && objectBAttr.get(k).getValue().equals("small")){
    					transformation.add("shrunk");
    				}
    				else if (objectAAttr.get(j).getValue().equals("0") && objectBAttr.get(k).getValue().equals("45")){
    					transformation.add("45 turn");
    				}
    				else if (objectAAttr.get(j).getName().equals("shape") && objectBAttr.get(k).getName().equals("shape")){
    					transformation.add(objectBAttr.get(k).getValue());
    				}

    			}
    		}
    	}
    	
    	//system.out.println(transformation);
    	return transformation;

    }
}
*/