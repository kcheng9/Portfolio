package project4;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.imageio.ImageIO;





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
    	//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
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
    public String Solve(VisualRavensProblem problem) {
    	String soln = "1";

    	HashMap<String, VisualRavensFigure> figures = problem.getFigures();
    	
    	VisualRavensFigure figureA = figures.get("A");
    	VisualRavensFigure figureB = figures.get("B");
    	VisualRavensFigure figureC = figures.get("C");
    	VisualRavensFigure figure1 = figures.get("1");
    	VisualRavensFigure figure2 = figures.get("2");
    	VisualRavensFigure figure3 = figures.get("3");
    	VisualRavensFigure figure4 = figures.get("4");
    	VisualRavensFigure figure5 = figures.get("5");
    	VisualRavensFigure figure6 = figures.get("6");
    	
    	BufferedImage figureAImage = null;
    	BufferedImage figureBImage = null;
    	BufferedImage figureCImage = null;
    	BufferedImage figure1Image = null;
    	BufferedImage figure2Image = null;
    	BufferedImage figure3Image = null;
    	BufferedImage figure4Image = null;
    	BufferedImage figure5Image = null;
    	BufferedImage figure6Image = null;
    	
    	try{
    		figureAImage = ImageIO.read(new File(figureA.getPath()));
    		figureBImage = ImageIO.read(new File(figureB.getPath()));
    		figureCImage = ImageIO.read(new File(figureC.getPath()));
    		figure1Image = ImageIO.read(new File(figure1.getPath()));
    		figure2Image = ImageIO.read(new File(figure2.getPath()));
    		figure3Image = ImageIO.read(new File(figure3.getPath()));
    		figure4Image = ImageIO.read(new File(figure4.getPath()));
    		figure5Image = ImageIO.read(new File(figure5.getPath()));
    		figure6Image = ImageIO.read(new File(figure6.getPath()));
    	} catch (IOException e){
    		
    	}
    	
    	
    	double diffAB = percentDiff(figureAImage, figureBImage);
    	double diffC1 = percentDiff(figureCImage, figure1Image);
    	double diffC2 = percentDiff(figureCImage, figure2Image);
    	double diffC3 = percentDiff(figureCImage, figure3Image);
    	double diffC4 = percentDiff(figureCImage, figure4Image);
    	double diffC5 = percentDiff(figureCImage, figure5Image);
    	double diffC6 = percentDiff(figureCImage, figure6Image);
    	
    	int calcDiff = (int) calcDiff(diffAB, diffC1, diffC2, diffC3, diffC4, diffC5, diffC6) + 1;
    	
    	ArrayList<Double> calcDiffArray = calcDiffArray(diffAB, diffC1, diffC2, diffC3, diffC4, diffC5, diffC6);
    	ArrayList<Double> adjustPercentRGB = adjustPercentRGB(calcDiffArray, figureAImage, figureBImage, figureCImage, figure1Image, figure2Image, figure3Image,
    			figure4Image, figure5Image, figure6Image);
    	
    	//if (problem.getProblemType().equals("2x2 (Image)")){
    		//soln = Integer.toString(calcDiff);
    	//}
    	
    	soln = Integer.toString(blackDiffSoln(adjustPercentRGB)+1);
    		
    	double blackA=findBlacks(figureAImage);
    	double blackB=findBlacks(figureBImage);
    	double blackC=findBlacks(figureCImage);
    	double black1=findBlacks(figure1Image);
    	double black2=findBlacks(figure2Image);
    	double black3=findBlacks(figure3Image);
    	double black4=findBlacks(figure4Image);
    	double black5=findBlacks(figure5Image);
    	double black6=findBlacks(figure6Image);
    	
    	ArrayList<Double> blackDiff = blackDiff(blackA, blackB, blackC, black1, black2, black3, black4, black5, black6);
    	int blackDiffSoln = blackDiffSoln(blackDiff)+1;
    	if (problem.getProblemType().equals("2x1 (Image)")){
    		soln = Integer.toString(blackDiffSoln);
    	
	    	ArrayList<Double> adjustPercent = adjustPercent(blackDiff, figureAImage, figureBImage, figureCImage, figure1Image, figure2Image, figure3Image,
			figure4Image, figure5Image, figure6Image);
	    	int adjustPercentSoln = blackDiffSoln(adjustPercent)+1;
    		soln = Integer.toString(adjustPercentSoln);
    	}
/*    	double percentDiffAB = percentDiffBlack(figureAImage, figureBImage);
    	double percentDiffC1 = percentDiffBlack(figureCImage, figure1Image);
    	double percentDiffC2 = percentDiffBlack(figureCImage, figure2Image);
    	double percentDiffC3 = percentDiffBlack(figureCImage, figure3Image);
    	double percentDiffC4 = percentDiffBlack(figureCImage, figure4Image);
    	double percentDiffC5 = percentDiffBlack(figureCImage, figure5Image);
    	double percentDiffC6 = percentDiffBlack(figureCImage, figure6Image);
    	
    	int calcDiffBlack = (int) calcDiff(percentDiffAB, percentDiffC1, percentDiffC2, percentDiffC3, percentDiffC4, percentDiffC5, percentDiffC6) + 1;
    	
    	soln = Integer.toString(calcDiffBlack);*/
    	
    	boolean[][] figureABool = changeToBlackWhite(figureAImage);
    	boolean[][] figureBBool = changeToBlackWhite(figureBImage);
    	boolean[][] figureCBool = changeToBlackWhite(figureCImage);
    	boolean[][] figure1Bool = changeToBlackWhite(figure1Image);
    	boolean[][] figure2Bool = changeToBlackWhite(figure2Image);
    	boolean[][] figure3Bool = changeToBlackWhite(figure3Image);
    	boolean[][] figure4Bool = changeToBlackWhite(figure4Image);
    	boolean[][] figure5Bool = changeToBlackWhite(figure5Image);
    	boolean[][] figure6Bool = changeToBlackWhite(figure6Image);/*
    	
    	//printBoolFigure(expectedSoln(figureABool, figureBBool, figureCBool));
    	
    	
    	boolean[][] expectedSoln = expectedSoln(figureABool, figureBBool, figureCBool);
    	
    	soln = pickBool(expectedSoln, figure1Bool, figure2Bool, figure3Bool, figure4Bool, figure5Bool, figure6Bool);
    	
    	BufferedImage scaledFigure = affine(figureBImage);
    	
    	boolean[][] scaledFigureBool = changeToBlackWhite(scaledFigure);*/
    	
/*		double boolCompAB = boolComp(figureABool, figureBBool);
		double boolCompAC = boolComp(figureABool, figureCBool);
		double boolCompC1 = boolComp(figureCBool, figure1Bool);
		double boolCompC2 = boolComp(figureCBool, figure2Bool);
		double boolCompC3 = boolComp(figureCBool, figure3Bool);
		double boolCompC4 = boolComp(figureCBool, figure4Bool);
		double boolCompC5 = boolComp(figureCBool, figure5Bool);
		double boolCompC6 = boolComp(figureCBool, figure6Bool);
		double boolCompB1 = boolComp(figureBBool, figure1Bool);
		double boolCompB2 = boolComp(figureBBool, figure2Bool);
		double boolCompB3 = boolComp(figureBBool, figure3Bool);
		double boolCompB4 = boolComp(figureBBool, figure4Bool);
		double boolCompB5 = boolComp(figureBBool, figure5Bool);
		double boolCompB6 = boolComp(figureBBool, figure6Bool);
		
		ArrayList<Double> boolCompCs= new ArrayList<Double>();
		boolCompCs.add(boolCompC1);
		boolCompCs.add(boolCompC2);
		boolCompCs.add(boolCompC3);
		boolCompCs.add(boolCompC4);
		boolCompCs.add(boolCompC5);
		boolCompCs.add(boolCompC6);
		
		ArrayList<Double> boolCompBs= new ArrayList<Double>();
		boolCompBs.add(boolCompB1);
		boolCompBs.add(boolCompB2);
		boolCompBs.add(boolCompB3);
		boolCompBs.add(boolCompB4);
		boolCompBs.add(boolCompB5);
		boolCompBs.add(boolCompB6);		
		
		System.out.println(boolCompCChoices(boolCompAB, boolCompCs));
		System.out.println(boolCompBChoices(boolCompAC, boolCompBs));
		System.out.println(boolChoices(boolCompAB, boolCompAC, boolCompCs, boolCompBs));*/
    	
/*    	String affineTransform = affineCompare(figureAImage, figureBImage);
    	BufferedImage solnShould = applyToC(figureBImage, figureCImage, affineTransform);
    	soln = Integer.toString(compareCs(solnShould, figure1Image, figure2Image, figure3Image, figure4Image, figure5Image, figure6Image));*/
    	
    	if(problem.getName().equals("2x2 Basic Problem 05")){
/*    		printBoolFigure(figureABool);
    		System.out.println("");
    		printBoolFigure(figureBBool);
    		System.out.println("");
    		System.out.println(affineTransform);
    		System.out.println("");
    		printBoolFigure(changeToBlackWhite(solnShould));*/
    		//printBoolFigure(changeToBlackWhite(affineCompare(figureAImage, figureBImage)));
/*    		printBoolFigure(figureABool);
    		System.out.println("");
    		printBoolFigure(figureBBool);
    		System.out.println("");
    		printBoolFigure(figureCBool);
    		System.out.println("");
    		printBoolFigure(expectedSoln);*/
/*    		System.out.println(figureAImage.getRGB(100, 100));
    		System.out.println(figureBImage.getRGB(100, 100));
    		System.out.println(figureCImage.getRGB(100, 100));
    		System.out.println(figure1Image.getRGB(100, 100));*/
    	}
    	
    	
    	
/*    	ArrayList<Double> adjustPercent = adjustPercent(blackDiff, figureAImage, figureBImage, figureCImage, figure1Image, figure2Image, figure3Image,
    			figure4Image, figure5Image, figure6Image);
    	int adjustPercentSoln = blackDiffSoln(adjustPercent)+1;
    	
    	if (problem.getProblemType().equals("2x1 (Image)")){
    		soln = Integer.toString(adjustPercentSoln);
    	}*/
    	
    	if(problem.getProblemType().equals("3x3 (Image)")){
    		soln = Integer.toString(blackDiffSoln);
        	
	    	ArrayList<Double> adjustPercent = adjustPercent(blackDiff, figureAImage, figureBImage, figureCImage, figure1Image, figure2Image, figure3Image,
			figure4Image, figure5Image, figure6Image);
	    	int adjustPercentSoln = blackDiffSoln(adjustPercent)+1;
    		soln = Integer.toString(adjustPercentSoln);
    		
        	VisualRavensFigure figureD = figures.get("D");
        	VisualRavensFigure figureE = figures.get("E");
        	VisualRavensFigure figureF = figures.get("F");
        	VisualRavensFigure figureG = figures.get("G");
        	VisualRavensFigure figureH = figures.get("H");
        	
        	BufferedImage figureDImage = null;
        	BufferedImage figureEImage = null;
        	BufferedImage figureFImage = null;
        	BufferedImage figureGImage = null;
        	BufferedImage figureHImage = null;
        	
        	try{
        		figureDImage = ImageIO.read(new File(figureD.getPath()));
        		figureEImage = ImageIO.read(new File(figureE.getPath()));
        		figureFImage = ImageIO.read(new File(figureF.getPath()));
        		figureGImage = ImageIO.read(new File(figureG.getPath()));
        		figureHImage = ImageIO.read(new File(figureH.getPath()));
        	} catch (IOException e){
        		
        	}
        	//blackA, B, C, 1, 2, 3, 4, 5, 6
        	double blackD=findBlacks(figureDImage);
        	double blackE=findBlacks(figureEImage);
        	double blackF=findBlacks(figureFImage);
        	double blackG=findBlacks(figureGImage);
        	double blackH=findBlacks(figureHImage);
        	
        	ArrayList<Double> blackArrayProbs = new ArrayList<Double>();
        	ArrayList<Double> blackArraySolns = new ArrayList<Double>();
        	blackArrayProbs.add(blackA);
        	blackArrayProbs.add(blackB);
        	blackArrayProbs.add(blackC);
        	blackArrayProbs.add(blackD);
        	blackArrayProbs.add(blackE);
        	blackArrayProbs.add(blackF);
        	blackArrayProbs.add(blackG);
        	blackArrayProbs.add(blackH);
        	blackArraySolns.add(black1);
        	blackArraySolns.add(black2);
        	blackArraySolns.add(black3);
        	blackArraySolns.add(black4);
        	blackArraySolns.add(black5);
        	blackArraySolns.add(black6);
        	
        	//System.out.println(blackArrayProbs);
        	//System.out.println(blackArraySolns);
        	
        	soln = Integer.toString(solnFinder3x3(blackArrayProbs, blackArraySolns));
    	}
    	return soln;
    }
    
    private int blackSoln3x3(ArrayList<Double> blackSolns, double closest){
    	ArrayList<Double> solns = new ArrayList<Double>();
    	
    	for (int i = 0; i < blackSolns.size(); i++){
    		solns.add(Math.abs(blackSolns.get(i)-closest));
    	}
    	
    	return solns.indexOf(Collections.min(solns)) +1;
    }
    
    private int solnFinder3x3(ArrayList<Double> blackArrayProbs, ArrayList<Double> blackArraySolns){
    	
    	ArrayList<Double> likeA = new ArrayList<Double>();
    	ArrayList<Double> likeRows = new ArrayList<Double>();
    	ArrayList<Double> likeDiags = new ArrayList<Double>();
    	int likeACount = 0, likeRowsCount = 0, likeDiagsCount = 0;
    	int soln = -1;
    	
    	
    	for (int i = 0; i < blackArrayProbs.size(); i++){
    		//if all are like the first, find the one closest to first
    		likeA.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(0)));
    		
    		if (i >= 0 && i <= 2){
    			likeRows.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(0)));
    		}
    		if (i > 2 && i <= 5){
    			likeRows.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(3)));
    		}
    		if (i > 5){
    			likeRows.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(6)));
    		}
    		
    		if (i == 0 || i == 4){
    			likeDiags.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(0)));
    		}
    		if (i == 1 || i == 5 || i == 6){
    			likeDiags.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(1)));
    		}
    		if (i == 2 || i == 3 || i == 7){
    			likeDiags.add(Math.abs(blackArrayProbs.get(i) - blackArrayProbs.get(2)));
    		}
    		
    	}
    	
    	//System.out.println(likeDiags);
    	
    	for (int j = 0; j < likeA.size(); j++){
    		if (likeA.get(j) <= 1){
    			
    		}
    		else{
    			likeACount++;
    		}
    		if (likeRows.get(j) <= 1){
    			
    		}
    		else{
    			likeRowsCount++;
    		}
    		if (likeDiags.get(j) <= 1){
    			
    		}
    		else{
    			likeDiagsCount++;
    		}
    	}
    	
    	if (likeACount == 0){
    		soln = blackSoln3x3(blackArraySolns, blackArrayProbs.get(0));
    	}
    	else if (likeRowsCount == 0){
    		soln = blackSoln3x3(blackArraySolns, blackArrayProbs.get(6));
    	}
    	if (likeDiagsCount == 0){
    		soln = blackSoln3x3(blackArraySolns, blackArrayProbs.get(0));
    	}
    	
    	//System.out.println(soln);
    	
    	if (soln == -1){
    		if (blackArrayProbs.get(0) < blackArrayProbs.get(1) && blackArrayProbs.get(1) < blackArrayProbs.get(2)
    				&& blackArrayProbs.get(3) < blackArrayProbs.get(4) && blackArrayProbs.get(4) < blackArrayProbs.get(5)
    				&& blackArrayProbs.get(6) < blackArrayProbs.get(7)){
    			for (int l = 0; l < blackArraySolns.size(); l++){
    				if (blackArrayProbs.get(7) < blackArraySolns.get(l)){
    					soln = l+1;
    				}
    			}
    		}
    	}
    	
    	if (soln == -1){
    		if (blackArrayProbs.get(0) > blackArrayProbs.get(1) && blackArrayProbs.get(1) > blackArrayProbs.get(2)
    				&& blackArrayProbs.get(3) > blackArrayProbs.get(4) && blackArrayProbs.get(4) > blackArrayProbs.get(5)
    				&& blackArrayProbs.get(6) > blackArrayProbs.get(7)){
    			for (int l = 0; l < blackArraySolns.size(); l++){
    				if (blackArrayProbs.get(5) > blackArraySolns.get(l)+1){
    					soln = l+1;
    				}
    			}
    		}
    	}
    	
    	if (soln == -1){
    		soln = 1;
    	}
    	
    	return soln;
    }
    
/*    private ArrayList<Double> blackDiff3x3(double blackA, double blackB, double blackC, double blackD, double blackE, double blackF, double blackG, double blackH, 
    		double black1, double black2, double black3, double black4, double black5, double black6){
    	
    	ArrayList<Double> diffs = new ArrayList<Double>();
    	
    }*/
    
    private boolean[][] boolPoints(ArrayList<Point> shape){
    	
    	boolean[][] boolPoints = new boolean[184][184];
    	
    	
    	for (int i = 0; i < shape.size(); i++){
    		boolPoints[shape.get(i).x][shape.get(i).y] = true;
    	}
    	
    	return boolPoints;
    }
    
    private ArrayList<Point> shapeIdent(boolean[][] figure){
    	ArrayList<Point> shape = new ArrayList<Point>();
    	
    	for (int y = 1; y < figure.length; y++){
    		for (int x = 1; x < figure.length; x++){
    			if (figure[x][y] == true){
    				if (figure[x-1][y] || figure[x][y-1]){
    					shape.add(new Point(x,y));
    				}
    			}
    		}
    	}
    	
    	return shape;
    }
    
    private ArrayList<Double> adjustPercentRGB (ArrayList<Double> calcDiff, BufferedImage imgA, BufferedImage imgB,
    		BufferedImage imgC, BufferedImage img1, BufferedImage img2, BufferedImage img3, BufferedImage img4, BufferedImage img5, BufferedImage img6){
    	
    	ArrayList<Double> adjustPercent = calcDiff;
    	
    	int x = imgA.getWidth()/2;
    	int y = imgA.getHeight()/2;
    	//System.out.println("calcDiff"+calcDiff);
    	if (imgA.getRGB(x,y) == -1){
    		if (imgB.getRGB(x,y) == -1){
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) == -1){
        			adjustPercent.set(0, calcDiff.get(0)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img2.getRGB(x,y) == -1){
        			adjustPercent.set(1, calcDiff.get(1)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img3.getRGB(x,y) == -1){
        			adjustPercent.set(2, calcDiff.get(2)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img4.getRGB(x,y) == -1){
        			adjustPercent.set(3, calcDiff.get(3)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img5.getRGB(x,y) == -1){
        			adjustPercent.set(4, calcDiff.get(4)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img6.getRGB(x,y) == -1){
        			adjustPercent.set(5, calcDiff.get(5)/3000);
        		}
    		}
    		else if (imgB.getRGB(x,y) != -1){
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(0, calcDiff.get(0)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(1, calcDiff.get(1)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(2, calcDiff.get(2)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(3, calcDiff.get(3)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(4, calcDiff.get(4)/3000);
        		}
        		if (imgC.getRGB(x,y) == -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(5, calcDiff.get(5)/3000);
        		}
    		}
    	}
    	else if (imgA.getRGB(x,y) != -1){
    		if (imgB.getRGB(x,y) == -1){
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) == -1){
        			adjustPercent.set(0, calcDiff.get(0)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img2.getRGB(x,y) == -1){
        			adjustPercent.set(1, calcDiff.get(1)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img3.getRGB(x,y) == -1){
        			adjustPercent.set(2, calcDiff.get(2)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img4.getRGB(x,y) == -1){
        			adjustPercent.set(3, calcDiff.get(3)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img5.getRGB(x,y) == -1){
        			adjustPercent.set(4, calcDiff.get(4)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img6.getRGB(x,y) == -1){
        			adjustPercent.set(5, calcDiff.get(5)/3000);
        		}
    		}
    		else if (imgB.getRGB(x,y) != -1){
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(0, calcDiff.get(0)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(1, calcDiff.get(1)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(2, calcDiff.get(2)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(3, calcDiff.get(3)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(4, calcDiff.get(4)/3000);
        		}
        		if (imgC.getRGB(x,y) != -1 && img1.getRGB(x,y) != -1){
        			adjustPercent.set(5, calcDiff.get(5)/3000);
        		}
    		}
    	}
    	//System.out.println("adjustPe" + adjustPercent);
    	return adjustPercent;
    }
    
    private ArrayList<Integer> boolChoices(double boolCompAB, double boolCompAC, ArrayList<Double> boolCompCs, ArrayList<Double> boolCompBs){
    	ArrayList<Integer> boolChoices = new ArrayList<Integer>();
    	
    	if (boolCompAB <= boolCompAC){
    		for (int i = 0; i < boolCompCs.size(); i++){
    			if (boolCompCs.get(i) <= boolCompBs.get(i)){
    				boolChoices.add(i+1);
    			}
    		}
    	}
    	else if (boolCompAB > boolCompAC){
    		for (int j = 0; j < boolCompCs.size(); j++){
    			if (boolCompCs.get(j) > boolCompBs.get(j)){
    				boolChoices.add(j+1);
    			}
    		}
    	}
    	
    	return boolChoices;
    }
    
    private ArrayList<Integer> boolCompBChoices(double boolCompAB, ArrayList<Double> boolCompCs){
    	ArrayList<Integer> boolCompCChoices = new ArrayList<Integer>();
    	
    	for (int i = 0; i < boolCompCs.size(); i++){
    		if (Math.abs(boolCompAB - boolCompCs.get(i)) < .01){
    			boolCompCChoices.add(i+1);
    		}
    	}
    	
    	return boolCompCChoices;
    }
    
    private ArrayList<Integer> boolCompCChoices(double boolCompAB, ArrayList<Double> boolCompCs){
    	ArrayList<Integer> boolCompCChoices = new ArrayList<Integer>();
    	
    	for (int i = 0; i < boolCompCs.size(); i++){
    		if (Math.abs(boolCompAB - boolCompCs.get(i)) < .03){
    			boolCompCChoices.add(i+1);
    		}
    	}
    	
    	return boolCompCChoices;
    }
    
    private double boolComp(boolean[][] figure1, boolean[][] figure2){
    	double pct;
    	double oneCount = 0.0, twoCount = 0.0;
    	
    	for (int x = 0; x < figure1.length; x++){
    		for (int y = 0; y < figure1.length; y++){
    			if (figure1[x][y]){
    				oneCount++;
    			}
    			if (figure2[x][y]){
    				twoCount++;
    			}
    		}
    	}
    	
    	pct = (oneCount-twoCount) / (figure1.length*figure1.length);
    	
    	return pct;
    }
    
    private int countBools(boolean[][] expected, boolean[][] figure){
    	int countExp = 0;
    	int countFig = 0;
    	
    	for(int x = 0; x < expected.length; x++){
    		for (int y = 0; y < expected.length; y++){
    			if (expected[x][y]){
    				countExp++;
    			}
    			if (figure[x][y]){
    				countFig++;
    			}
    		}
    	}
    	
    	int count = Math.abs(countExp - countFig);
    	
    	return count;
    }
    
    private int compareCs(BufferedImage solnShould, BufferedImage fig1, BufferedImage fig2, BufferedImage fig3, BufferedImage fig4, 
    		BufferedImage fig5, BufferedImage fig6){
    	
    	double C1 = negDiffs(solnShould, fig1);
    	double C2 = negDiffs(solnShould, fig2);
    	double C3 = negDiffs(solnShould, fig3);
    	double C4 = negDiffs(solnShould, fig4);
    	double C5 = negDiffs(solnShould, fig5);
    	double C6 = negDiffs(solnShould, fig6);
    	
    	ArrayList<Double> Cs = new ArrayList<Double>();
    	Cs.add(C1);
    	Cs.add(C2);
    	Cs.add(C3);
    	Cs.add(C4);
    	Cs.add(C5);
    	Cs.add(C6);
    	
    	return Cs.indexOf(Collections.min(Cs))+1;
    }
    
    private BufferedImage applyToC(BufferedImage figureB, BufferedImage figureC, String affineCompare){
    	if (affineCompare == "identity"){
    		return figureC;
    	}
    	else if (affineCompare == "affine90"){
    		return affineRotate(90, figureC);
    	}
    	else if (affineCompare == "affine180"){
    		return affineRotate(180, figureC);
    	}
    	else if (affineCompare == "affine270"){
    		return affineRotate(270, figureC);
    	}
    	else if (affineCompare == "flip"){
    		return affineFlip(figureC);
    	}
    	else if (affineCompare == "rotate90flip"){
    		return affineRotate(90, affineFlip(figureC));
    	}
    	else if (affineCompare == "rotate180flip"){
    		return affineRotate(180, affineFlip(figureC));
    	}
    	else if (affineCompare == "rotate270flip"){
    		return affineRotate(270, affineFlip(figureC));
    	}
    	else{
    		return figureC;
    	}
    }
    
    private String affineCompare(BufferedImage origFigure, BufferedImage figure2){
    	
    	BufferedImage identity = figure2;
    	
    	BufferedImage affine90 = affineRotate(90, origFigure);
    	
    	BufferedImage affine180 = affineRotate(180, origFigure);
    	
    	BufferedImage affine270 = affineRotate(270, origFigure);
    	
    	BufferedImage flip = affineFlip(origFigure);
    	
    	BufferedImage rotate90flip = affineRotate(90, flip);
    	
    	BufferedImage rotate180flip = affineRotate(180, flip);
    	
    	BufferedImage rotate270flip = affineRotate(270, flip);
    	
    	double AB = negDiffs(identity, figure2);
    	double AB90 = negDiffs(affine90, figure2);
    	double AB180 = negDiffs(affine180, figure2);
    	double AB270 = negDiffs(affine270, figure2);
    	double ABflip = negDiffs(flip, figure2);
    	double ABflip90 = negDiffs(rotate90flip, figure2);
    	double ABflip180 = negDiffs(rotate180flip, figure2);
    	double ABflip270 = negDiffs(rotate270flip, figure2);
    	
    	ArrayList<Double> affineDiffArray = affineDiffArray(AB, AB90, AB180, AB270, ABflip, ABflip90, ABflip180, ABflip270);
    	
    	int index = affineDiffArray.indexOf(Collections.min((affineDiffArray)));
    	
    	if (index == 0){
    		return "identity";
    	}
    	else if (index == 1){
    		return "affine90";
    	}
    	else if (index == 2){
    		return "affine180";
    	}
    	else if (index == 3){
    		return "affine270";
    	}
    	else if (index == 4){
    		return "flip";
    	}
    	else if (index == 5){
    		return "rotate90flip";
    	}
    	else if (index == 6){
    		return "rotate180flip";
    	}
    	else if (index == 7){
    		return "rotate270flip";
    	}
    	else{
    		return "identity";
    	}
    	
    }
    
    private ArrayList<Double> affineDiffArray(double diffAB, double diffC1, double diffC2, double diffC3, double diffC4, double diffC5, double diffC6, double diffC7){
    	ArrayList<Double> diffCs = new ArrayList<Double>();
	
		diffCs.add(diffC1);
		diffCs.add(diffC2);
		diffCs.add(diffC3);
		diffCs.add(diffC4);
		diffCs.add(diffC5);
		diffCs.add(diffC6);
		diffCs.add(diffC7);
	
	//System.out.println("diffCs"+diffCs);
	
		ArrayList<Double> diff = new ArrayList<Double>();
		for (int i = 0; i < 7; i++){
			diff.add(Math.abs(diffCs.get(i)));
		}
		
		return diff;
    }
    
    private BufferedImage affineFlip(BufferedImage figure){
    	int w = figure.getWidth();
    	int h = figure.getHeight();
    	
    	BufferedImage after = new BufferedImage(w,h, figure.getType());
    	AffineTransform at = new AffineTransform();
    	
    	at = at.getScaleInstance(-1,1);
    	at.translate(-w, 0);
    	
    	AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
    	after = scaleOp.filter(figure, after);
    	
    	return after;
    }
    
    private BufferedImage affineRotate(int angle, BufferedImage figure){
    	int w = figure.getWidth();
    	int h = figure.getHeight();
    	
    	BufferedImage after = new BufferedImage(w,h, figure.getType());
    	AffineTransform at = new AffineTransform();
    	
    	at.rotate(Math.toRadians(angle), w/2, h/2);
    	
    	AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
    	after = scaleOp.filter(figure, after);
    	
/*    	for (int i = 0; i <= w/4; i++){
    		for (int j = 0; j <= h/4; j++){
    			after.setRGB(i, j, -1);
    		}
    	}
    	
    	for (int k = 3*w/4; k < w; k++){
    		for (int l = 3*h/4; l < h; l++){
    			after.setRGB(k,l,-1);
    		}
    	}
    	
    	for (int k = 3*w/4; k < w; k++){
    		for (int l = 0; l < h/4; l++){
    			after.setRGB(k,l,-1);
    		}
    	}
    	
    	for (int k = 0; k < w/4; k++){
    		for (int l = 3*h/4; l < h; l++){
    			after.setRGB(k,l,-1);
    		}
    	}*/
    	
    	return after;
    }
    
    private String pickBool(boolean[][] expected, boolean[][] figure1, boolean[][] figure2, boolean[][] figure3,
    		boolean[][] figure4, boolean[][] figure5, boolean[][] figure6){
    	
    	ArrayList<Integer> boolCount = new ArrayList<Integer>();
    	
    	boolCount.add(countBools(expected, figure1));
    	boolCount.add(countBools(expected, figure2));
    	boolCount.add(countBools(expected, figure3));
    	boolCount.add(countBools(expected, figure4));
    	boolCount.add(countBools(expected, figure5));
    	boolCount.add(countBools(expected, figure6));
    	
    	Integer ans = boolCount.indexOf(Collections.min(boolCount))+1;
    	
    	return Integer.toString(ans);
    	
    }
    
    private void printBoolFigure(boolean[][] figure){
    	for (int i = 0; i< figure.length; i+=4){
    		System.out.println("");
    		for (int j = 0; j< figure.length; j+=4){
    			if (figure[i][j] == false){
    				System.out.print(".");
    			}
    			else if (figure[i][j] == true){
    				System.out.print("@");
    			}
    			
    		}
    	}
    }
    
    private boolean[][] expectedSoln(boolean[][] figureABool, boolean[][] figureBBool, boolean[][] figureCBool){
    	
    	int width = figureABool.length;
    	int height = figureABool.length;
    	
    	boolean[][] expected = new boolean[width][height];
    	
    	for (int y = 0; y < height; y++){
    		for (int x = 0; x < width; x++){
    			if (figureABool[x][y] == figureBBool[x][y]){
    				expected[x][y] = figureCBool[x][y];
    			}
    			else if (figureABool[x][y] == true && figureBBool[x][y] == false){
    				expected[x][y] = false;
    			}
    			else if (figureABool[x][y] == false && figureBBool[x][y] == true){
    				expected[x][y] = true;
    			}
    		}
    	}
    	
    	return expected;
    }
    
    
    private double percentDiffBlack(BufferedImage image1, BufferedImage image2){
    	int width1 = image1.getWidth(null);
        int width2 = image2.getWidth(null);
        int height1 = image1.getHeight(null);
        int height2 = image2.getHeight(null);
        if ((width1 != width2) || (height1 != height2)) {
          System.err.println("Error: Images dimensions mismatch");
          System.exit(1);
        }
        long diff = 0;
        for (int y = 0; y < height1; y++) {
          for (int x = 0; x < width1; x++) {
        	int blackDiff = 0;
            int rgb1 = image1.getRGB(x, y);
            int rgb2 = image2.getRGB(x, y);
            
            if (((rgb1 & 0x00FFFFFF)==0) ){
            	if (!((rgb2 & 0x00FFFFFF)==0)){
            		blackDiff++;
            	}
            }
          }
          
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        //System.out.println("diff percent: " + (p * 100.0));
        
        return p*100.0;
    }
    
    private ArrayList<Double> adjustPercent (ArrayList<Double> blackDiff, BufferedImage imgA, BufferedImage imgB,
    		BufferedImage imgC, BufferedImage img1, BufferedImage img2, BufferedImage img3, BufferedImage img4, BufferedImage img5, BufferedImage img6){
    	
    	int x = imgA.getHeight()/2;
    	int y = imgA.getWidth()/2;
    	
    	ArrayList<Double> adjustPercent = blackDiff;
    	
    	if (imgA.getRGB(x, y) == imgB.getRGB(x,y)){
    		//System.out.println("This happened");
    		if (imgC.getRGB(x,y) == img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) == img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) == img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) == img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) == img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) == img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}
    	else{
    		if (imgC.getRGB(x,y) != img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) != img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) != img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) != img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) != img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) != img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}
    	
/*    	x = imgA.getHeight()/4;
    	y = imgA.getWidth()/4;
    	
    	if (imgA.getRGB(x, y) == imgB.getRGB(x,y)){
    		//System.out.println("This happened");
    		if (imgC.getRGB(x,y) == img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/1000);
    		}
    		if (imgC.getRGB(x,y) == img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/1000);
    		}
    		if (imgC.getRGB(x,y) == img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/1000);
    		}
    		if (imgC.getRGB(x,y) == img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/1000);
    		}
    		if (imgC.getRGB(x,y) == img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/1000);
    		}
    		if (imgC.getRGB(x,y) == img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/1000);
    		}
    	}
    	else{
    		if (imgC.getRGB(x,y) != img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/1000);
    		}
    		if (imgC.getRGB(x,y) != img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/1000);
    		}
    		if (imgC.getRGB(x,y) != img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/1000);
    		}
    		if (imgC.getRGB(x,y) != img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/1000);
    		}
    		if (imgC.getRGB(x,y) != img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/1000);
    		}
    		if (imgC.getRGB(x,y) != img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/1000);
    		}
    	}*/
    	
    	/*x = imgA.getHeight()/4*3;
    	y = imgA.getWidth()/4*3;
    	
    	if (imgA.getRGB(x, y) == imgB.getRGB(x,y)){
    		//System.out.println("This happened");
    		if (imgC.getRGB(x,y) == img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) == img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) == img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) == img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) == img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) == img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}
    	else{
    		if (imgC.getRGB(x,y) != img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) != img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) != img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) != img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) != img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) != img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}*/
    	
    	/*x = imgA.getHeight()/4*3;
    	y = imgA.getWidth()/4;
    	
    	if (imgA.getRGB(x, y) == imgB.getRGB(x,y)){
    		//System.out.println("This happened");
    		if (imgC.getRGB(x,y) == img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) == img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) == img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) == img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) == img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) == img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}
    	else{
    		if (imgC.getRGB(x,y) != img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) != img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) != img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) != img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) != img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) != img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}*/
    	
/*    	x = imgA.getHeight()/4;
    	y = imgA.getWidth()/4*3;
    	
    	if (imgA.getRGB(x, y) == imgB.getRGB(x,y)){
    		//System.out.println("This happened");
    		if (imgC.getRGB(x,y) == img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) == img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) == img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) == img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) == img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) == img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}
    	else{
    		if (imgC.getRGB(x,y) != img1.getRGB(x,y)){
    			adjustPercent.set(0, blackDiff.get(0)/3000);
    		}
    		if (imgC.getRGB(x,y) != img2.getRGB(x,y)){
    			adjustPercent.set(1, blackDiff.get(1)/3000);
    		}
    		if (imgC.getRGB(x,y) != img3.getRGB(x,y)){
    			adjustPercent.set(2, blackDiff.get(2)/3000);
    		}
    		if (imgC.getRGB(x,y) != img4.getRGB(x,y)){
    			adjustPercent.set(3, blackDiff.get(3)/3000);
    		}
    		if (imgC.getRGB(x,y) != img5.getRGB(x,y)){
    			adjustPercent.set(4, blackDiff.get(4)/3000);
    		}
    		if (imgC.getRGB(x,y) != img6.getRGB(x,y)){
    			adjustPercent.set(5, blackDiff.get(5)/3000);
    		}
    	}*/
    	return adjustPercent;
    	
    }
    
    private int blackDiffSoln(ArrayList<Double> blackDiff){
    	return blackDiff.indexOf(Collections.min(blackDiff));
    }
    
    private ArrayList<Double> blackDiff(double a, double b, double c, double c1, double c2, double c3, double c4, double c5, double c6){
    	double ab = Math.abs(a-b);
    	//System.out.println("ab"+ab);
    	ArrayList<Double> cs = new ArrayList<Double>();
    	cs.add(Math.abs(c-c1));
    	cs.add(Math.abs(c-c2));
    	cs.add(Math.abs(c-c3));
    	cs.add(Math.abs(c-c4));
    	cs.add(Math.abs(c-c5));
    	cs.add(Math.abs(c-c5));
    	//System.out.println("cs"+cs);
    	for (int i = 0; i < cs.size(); i++){
    		cs.set(i, Math.abs(cs.get(i)-ab));
    	}
    	
    	return cs;
    }
    private double findBlacks(BufferedImage img){
    	final int xmin = img.getMinX();
    	final int ymin = img.getMinY();

    	final int ymax = ymin + img.getHeight();
    	final int xmax = xmin + img.getWidth();

    	int blackCount=0;
    	for (int i = xmin;i<xmax;i++)
    	{
    	   for (int j = ymin;j<ymax;j++)
    	   {

    	    int pixel = img.getRGB(i, j);

    	    if ((pixel & 0x00FFFFFF) == 0)
    	    {
    	        blackCount++;
    	    }
    	   }
    	}
    	
    	double percent = (double) blackCount / (img.getHeight() * img.getWidth()) * 100;
    	
    	
    	return percent;
    }
    
    private ArrayList<Double> calcDiffArray(double diffAB, double diffC1, double diffC2, double diffC3, double diffC4, double diffC5, double diffC6){
    	ArrayList<Double> diffCs = new ArrayList<Double>();
	
		diffCs.add(diffC1);
		diffCs.add(diffC2);
		diffCs.add(diffC3);
		diffCs.add(diffC4);
		diffCs.add(diffC5);
		diffCs.add(diffC6);
	
	//System.out.println("diffCs"+diffCs);
	
		ArrayList<Double> diff = new ArrayList<Double>();
		for (int i = 0; i < 6; i++){
			diff.add(Math.abs(diffAB-diffCs.get(i)));
		}
		
		return diff;
    }
    
    private double calcDiff(double diffAB, double diffC1, double diffC2, double diffC3, double diffC4, double diffC5, double diffC6){
    	
    	//System.out.println("diffAB"+diffAB);
    	
    	ArrayList<Double> diffCs = new ArrayList<Double>();
    	
    	diffCs.add(diffC1);
    	diffCs.add(diffC2);
    	diffCs.add(diffC3);
    	diffCs.add(diffC4);
    	diffCs.add(diffC5);
    	diffCs.add(diffC6);
    	
    	//System.out.println("diffCs"+diffCs);
    	
    	ArrayList<Double> diff = new ArrayList<Double>();
    	for (int i = 0; i < 6; i++){
    		diff.add(Math.abs(diffAB-diffCs.get(i)));
    	}
    	
    	//System.out.println(diff);
    	//System.out.println(diff.indexOf(Collections.max(diff)));
    	
    	return diff.indexOf(Collections.min(diff));
    	
    }
    
    private double negDiffs(BufferedImage img1, BufferedImage img2){
    	int w1 = img1.getWidth();
    	int w2 = img2.getWidth();
    	int h1 = img1.getHeight();
    	int h2 = img2.getHeight();
    	
    	boolean[][] img1Bool = changeToBlackWhite(img1);
    	boolean[][] img2Bool = changeToBlackWhite(img2);
    	
    	double diff = 0.0;
    	
    	for (int y = 0; y < h1; y++){
    		for (int x = 0; x < w1; x++){
    			if (img1Bool[x][y] == img2Bool[x][y]){
    				diff -= 1;
    			}
    		}
    	}
    	
    	return diff;
    }
    
    private double percentDiff(BufferedImage figureAImage, BufferedImage figureBImage){
    	int width1 = figureAImage.getWidth(null);
        int width2 = figureBImage.getWidth(null);
        int height1 = figureAImage.getHeight(null);
        int height2 = figureBImage.getHeight(null);
        if ((width1 != width2) || (height1 != height2)) {
          System.err.println("Error: Images dimensions mismatch");
          System.exit(1);
        }
        long diff = 0;
        for (int y = 0; y < height1; y++) {
          for (int x = 0; x < width1; x++) {
            int rgb1 = figureAImage.getRGB(x, y);
            int rgb2 = figureBImage.getRGB(x, y);
            int r1 = (rgb1 >> 16) & 0xff;
            int g1 = (rgb1 >>  8) & 0xff;
            int b1 = (rgb1      ) & 0xff;
            int r2 = (rgb2 >> 16) & 0xff;
            int g2 = (rgb2 >>  8) & 0xff;
            int b2 = (rgb2      ) & 0xff;
            diff += Math.abs(r1 - r2);
            diff += Math.abs(g1 - g2);
            diff += Math.abs(b1 - b2);
          }
          
        }
        double n = width1 * height1 * 3;
        double p = diff / n / 255.0;
        //System.out.println("diff percent: " + (p * 100.0));
        
        return p;
    }
    
    private boolean[][] changeToBlackWhite(BufferedImage image){
    	int width = image.getWidth();
    	int height = image.getHeight();
    	boolean[][] imageBool = new boolean[width][height];
    	
    	for (int y = 1; y < height-1; y++){
    		for (int x = 1; x < width-1; x++){
    			int pixel = image.getRGB(y,x);
    			if (pixel == -1){
    				imageBool[x][y] = false;
    			}
    			else{
    				imageBool[x][y] = true;
    			}
    		}
    	}
    	
    	return imageBool;
    }
    
    private byte[] convertToByte(BufferedImage image) throws IOException{
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	ImageIO.write(image, "jpg", baos);
    	baos.flush();
    	byte[] imageInByte = baos.toByteArray();
    	baos.close();
    	
    	return imageInByte;
    }
}
    	
    	