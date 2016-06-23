import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import jsatbox.miscellaneous.VariablesContainer;
import jsatbox.parsers.SATsolver;

public class MainEternity2SAT{
	public static Random r = new Random();
	
	public static void main(String[] args) throws Exception{
		String solver_path = "/home/jostie/tools/cryptominisat/build.unchanged/cryptominisat4.sh";
		String input_file = "/home/jostie/Desktop/eternity2.cnf";
		String output_file = "/home/jostie/Desktop/solution.cnf";
		
		// hash which contains position and orientation of already known tiles
		HashMap<String,int[]> known = new HashMap<String,int[]>();
		known.put("135", new int[]{138,1});	// this is already known from the game
		
		// which field positions to use for the constraints?
		// here all positions are used, counted from 0 to 255 (given as the four outer rings and the rest)
		String[] constrained_positions = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
				"16","31",
				"32","47",
				"48","63",
				"64","79",
				"80","95",
				"96","111",
				"112","127",
				"128","143",
				"144","159",
				"160","175",
				"176","191",
				"192","207",
				"208","223",
				"224","239",
				"240","241","242","243","244","245","246","247","248","249","250","251","252","253","254","255",
				"17","18","19","20","21","22","23","24","25","26","27","28","29","30",
				"33","46",
				"49","62",
				"65","78",
				"81","94",
				"97","110",
				"113","126",
				"129","142",
				"145","158",
				"161","174",
				"177","190",
				"193","206",
				"209","222",
				"225","226","227","228","229","230","231","232","233","234","235","236","237","238",
				"34","35","36","37","38","39","40","41","42","43","44","45",
				"50","61",
				"66","77",
				"82","93",
				"98","109",
				"114","125",
				"130","141",
				"146","157",
				"162","173",
				"178","189",
				"194","205",
				"210","211","212","213","214","215","216","217","218","219","220","221",
				"51","52","53","54","55","56","57","58","59","60",
				"67","76",
				"83","92",
				"99","108",
				"115","124",
				"131","140",
				"147","156",
				"163","172",
				"179","188",
				"195","196","197","198","199","200","201","202","203","204",
				"68","69","70","71","72","73","74","75",
				"84","85","86","87","88","89","90","91",
				"100","101","102","103","104","105","106","107",
				"116","117","118","119","120","121","122","123",
				"132","133","134","135","136","137","138","139",
				"148","149","150","151","152","153","154","155",
				"164","165","166","167","168","169","170","171",
				"180","181","182","183","184","185","186","187"
				};
		
		// make constraints and potential compute solution
		known = solveEternity2(solver_path, input_file, output_file, null, known, constrained_positions, null, 1);
		printTable(known);		
	}
	
	/**
	 * Prints solution.
	 * 
	 * @param known Known solution.
	 */
	public static void print(HashMap<String,int[]> known){
		Iterator<String> iterator = known.keySet().iterator();
		while (iterator.hasNext()){
			String key = iterator.next();
			int[] data = known.get(key);
			System.out.println(key + "\t" + data[0] + "\t" + data[1]);
		}
	}
	
	/**
	 * Prints solution in form of a board.
	 * 
	 * @param known Known solution.
	 */
	public static void printTable(HashMap<String,int[]> known){
		int[] table = new int[256];
		for (int i = 0; i < table.length; i++)
			table[i] = -1;
		Iterator<String> iterator = known.keySet().iterator();
		while (iterator.hasNext()){
			String key = iterator.next();
			int[] data = known.get(key);
			table[(new Integer(key)).intValue()] = data[0];
		}
		for (int i = 0; i < table.length; i++){
			if (i > 0 && i % 16 == 0)
				System.out.println();
			System.out.print((table[i]+1) + "\t");
		}
//		for (int i = 0; i < table.length - 1; i++)
//			if (table[i] > -1)
//				System.out.print((table[i]+1) + "\t");
//			else
//				System.out.print("NA" + "\t");
//		if (table[table.length-1] > -1)
//			System.out.println(table[table.length-1]+1);
//		else
//			System.out.println("NA");
	}
	
	/**
	 * Some helper method. Puts tiles into a more convenient form. Otherwise much typing for
	 * putting constrained positions into hash.
	 * 
	 * @param hm 
	 * @param indices
	 */
	public static void putIntoHash(HashMap<String,String> hm, String[] indices){
		for (int i = 0; i < indices.length; i++)
			hm.put(indices[i], indices[i]);
	}
	
	/**
	 * Corrects indices. Must think about that again. Forgot why.
	 * 
	 * @param l
	 * @param n
	 * @param n_
	 * @return
	 */
	public static int correct(int l, int n, int n_){
		int i = (int)(Math.floor(l/n_) - 1);
		int j_= l%n_;
		int j = j_ - 1;
		
		if (i < 0 || j_== 0 || j_ == 17)
			return -1;
		
		return n*i + j;
	}
	
	/**
	 * Writes constraints. Starts solver. Reads solution in. Assigns solution to variables.
	 * 
	 * @param solver_path Full path to SAT-solver binary.
	 * @param input_file Full path where to generate constraints file.
	 * @param output_file Full path where to write solution file.
	 * @param seed Some seed for e.g. cryptominisat.
	 * @param variables The variables of the problem.
	 * @param constraints The constraints of the problem.
	 * @param verbose If -1 then no output, if greater -1 then output.
	 * @return True if satisfiable, false otherwise.
	 * @throws Exception
	 */
	public static boolean solveWriteComputeRead(String solver_path, String input_file, String output_file, Integer seed, VariablesContainer variables, StringBuffer constraints, int verbose) throws Exception{
		// solve constraints
		int seed_ = r.nextInt();
		if (seed != null)
			seed_ = seed.intValue();
		
		// this call is for execting the solver
		SATsolver ss = new SATsolver(variables, solver_path, input_file, output_file, seed_);
		
		if (verbose > -1)
			System.out.print("executing " + ss.getCmd() + " ... ");
		long start_time = System.currentTimeMillis();
		
		String status = ss.execute(constraints, verbose > 0);
		//String status = ss.execute2(constraints, verbose > 0);
		
		long stop_time = System.currentTimeMillis();
		
		if (verbose > -1)
			System.out.println("finished (" + (stop_time - start_time) + " ms)");
		
		if (status.equals("UNSATISFIABLE"))
			return false;
		
		return true;
	}
	
	/**
	 * Writes constraints. Reads some externally computed solution in. Assigns solution to variables.
	 * 
	 * @param output_file Full path where to find solution file.
	 * @param seed Some seed for e.g. cryptominisat.
	 * @param variables The variables of the problem.
	 * @param constraints The constraints of the problem.
	 * @param verbose If -1 then no output, if greater -1 then output.
	 * @throws Exception
	 */
	public static void solveWriteRead(String output_file, VariablesContainer variables, StringBuffer constraints, int verbose) throws Exception{
		SATsolver ss = new SATsolver(variables, null, null, output_file, 0);
		
		if (verbose > -1)
			System.out.print("executing " + ss.getCmd() + " ... ");
		long start_time = System.currentTimeMillis();
		
		// this call is a dummy
		ss.execute2(constraints, verbose > 0);
		
		long stop_time = System.currentTimeMillis();
		
		if (verbose > -1)
			System.out.println("finished (" + (stop_time - start_time) + " ms)");
	}
	
	/**
	 * Creates constraints and potentially computes solution if a solver is provided.
	 * 
	 * @param solver_path Full path to SAT-solver binary.
	 * @param input_file Full path where to generate constraints file.
	 * @param output_file Full path where to write solution file.
	 * @param solution_file Full path to solution file (if available).
	 * @param known HashMap of known tiles on the board: <number of tile as string>, new int[]{<position>, <orientation>}
	 * @param constrained_positions Which field positions are used for the constraints.
	 * @param seed Some seed for e.g. cryptominisat.
	 * @param verbose If -1 then no output, if greater -1 then output.
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String,int[]> solveEternity2(String solver_path, String input_file, String output_file, String solution_file, HashMap<String,int[]> known, String[] constrained_positions, Integer seed, int verbose) throws Exception{
		HashMap<String,String> hm = new HashMap<String,String>();
		putIntoHash(hm, constrained_positions);						// make more convenient hash from list of constrained positions
		
		StringBuffer constraints = new StringBuffer();				// keeps the constraints
		VariablesContainer variables = new VariablesContainer();	// manages the variables
		
		int n = 16;	// board size
		
		String[] tiles = {
				"RS__", "RV__", "TS__", "ST__", "RNR_", "RFT_", "RAR_", "RAU_", "RIS_", "RLV_", "RJT_", "REV_", "REU_", "ROV_", "TFR_", "TCS_",	// 16
				"THU_", "TDU_", "TLT_", "TJT_", "TMV_", "TKR_", "TKU_", "TGR_", "TOR_", "SNT_", "SNS_", "SFS_", "SCS_", "SLU_", "SJT_", "SGS_",	// 32
				"SET_", "SEV_", "SQU_", "SPV_", "VCR_", "VBU_", "VIU_", "VDT_", "VDS_", "VJR_", "VJT_", "VJS_", "VMR_", "VGV_", "VEV_", "VQV_",	// 48
				"UNU_", "UFR_", "UFT_", "UAR_", "ULV_", "UMV_", "UMU_", "UES_", "UQR_", "UQU_", "UOT_", "UPS_", "CNNA", "NNHL", "FFBN", "CNEN",	// 64
				"CCPN", "HHNC", "IFNC", "ANCG", "PENC", "BBLN", "KNBL", "IHCN", "MNIJ", "GJNI", "BNIE", "JNDH", "DDJN", "BQNL", "GBNL", "LQON",	// 80
				"DCNJ", "CCNM", "IMNM", "KCDN", "AHNK", "EKNK", "KQGN", "NONG", "PNGA", "QNGM", "IKNE", "DJNE", "DMNE", "MONE", "KHNE", "OOBN",	// 96
				"DNPM", "GENP", "ANPO", "PPON", "JFFK", "FFKQ", "FFQD", "FFPA", "MJFC", "EFAB", "ADEF", "FAMJ", "QIFA", "FHJK", "KJFH", "GDFB",	// 112
				"QFBG", "MFIH", "LKFI", "IFIK", "QFIP", "BOFD", "KFLQ", "PFJE", "HPFM", "QFGA", "DFGH", "GGJF", "KPFE", "OJFE", "HAFQ", "QDOF",	// 128
				"MBFQ", "EFQG", "GFOA", "KHFO", "QFPH", "IMFP", "MBFP", "GFPQ", "LCCG", "AABC", "AAIC", "AAKC", "DOCA", "ACAJ", "QKCA", "OOCA",	// 144
				"HBMC", "CKCB", "CPCB", "BBHC", "JKCB", "CDAI", "MPCD", "IICL", "LIDC", "PQCL", "LLCM", "KCML", "PLCM", "QCGL", "EACG", "OOCE",	// 160
				"QAGC", "HGCQ", "JICP", "QCPM", "MAHB", "HMEA", "BBAI", "IJAD", "OADD", "MEAL", "QALG", "IALO", "AJJJ", "GAJK", "LOAM", "LDAK",	// 176
				"HMAG", "KQAE", "EEJA", "LQAQ", "OIQA", "LIAO", "DEHH", "LHBP", "KHID", "EEHI", "OLHD", "OHLH", "BDHL", "DHLQ", "BBHJ", "JJGH",	// 192
				"ILHM", "OIHK", "PHKO", "KPJH", "IPHG", "EHGD", "GGGH", "DBHE", "EHQO", "LHQP", "KDHO", "EHOK", "BHOQ", "OHOP", "BBPL", "PBID",	// 208
				"DBLI", "QJBL", "JBQB", "EEBM", "KBGB", "PBKM", "POBK", "JBGD", "MKBQ", "IMBO", "QBPI", "OOBP", "OPBP", "IIKP", "IIGL", "IIQJ",	// 224
				"BIEJ", "DJIL", "KKIL", "DEIM", "LPIG", "QEIG", "KGIE", "EKOI", "GDDD", "MDLQ", "MDML", "LGDM", "KJDM", "QDKM", "JPDG", "JDGO",	// 248
				"LODE", "MODE", "JJKL", "GELJ", "PGLM", "QPLO", "JJOP", "MJMK", "KMOJ", "OJGQ", "PEMM", "KMKE", "GKEQ", "OQGQ", "QPGP", "OPEP"	// 256
		};
		
		// get unique tile colors
		HashMap<String, String> for_sorting = new HashMap<String, String>();
		for (int i = 0; i < tiles.length; i++)
			for (int j = 0; j < tiles[i].length(); j++)
				for_sorting.put(tiles[i].charAt(j) + "", tiles[i].charAt(j) + "");
		
		// sort tile colors (better for understanding what is going on)
		String[] colors = new String[for_sorting.size()];
		for_sorting.keySet().toArray(colors);
		Arrays.sort(colors);
		
		// assign index of color
		HashMap<String, Integer> tile_colors = new HashMap<String, Integer>();
		for (int i = 0; i < colors.length; i++)
			tile_colors.put(colors[i], tile_colors.size());
		
		/*// output colors and corresponding index (only for debugging)
		colors = new String[tile_colors.size()];
		tile_colors.keySet().toArray(colors);
		for (int i = 0; i < colors.length; i++)
			System.out.println(colors[i] + "\t" + tile_colors.get(colors[i]));*/
		
		// ######################################################################
		//                          create variables
		// ######################################################################
		
		long start_time = System.currentTimeMillis();
		
		// for all tiles with all orientations
		if (verbose > -1)
			System.out.print("creating variables ... ");
		variables.add("tile", new int[]{
				tiles.length,		// tile
				4,					// orientation
				4,					// quadrant
				tile_colors.size() 	// color
				});
		
		// for helping
		variables.add("help", new int[]{
				tiles.length,
				4,
				4,
				tile_colors.size()
				});

		// for the chosen orientation
		variables.add("selected_orientation", new int[]{
				tiles.length,		// tile
				4					// orientation
				});
		
		// for the tile with chosen orientation
		variables.add("realized_tile_orientation", new int[]{
				tiles.length,		// tile
				4,					// quadrant
				tile_colors.size() 	// color
				});
		
		// for chosen field
		variables.add("selected_field", new int[]{
				tiles.length,
				(n+2)*(n+2)
				});
		
		// for tile chosen on a field
		variables.add("realized_tile_on_field", new int[]{
				tiles.length,		// tile
				(n+2)*(n+2),		// field
				4,					// quadrant
				tile_colors.size()	// color
				});
		
		// for the chosen orientation and tiles on the board
		variables.add("realized_tile_on_board", new int[]{
				(n+2)*(n+2),		// field
				4,					// quadrant
				tile_colors.size()	// color
				});
		
		long stop_time = System.currentTimeMillis();
		if (verbose > -1)
			System.out.println("finished (" + (stop_time - start_time) + " ms)");
		
		// ######################################################################
		//                         create constraints
		// ######################################################################
		
		// create constraints for everything what is already known
		Iterator<String> iterator = known.keySet().iterator();
		while (iterator.hasNext()){
			String key = iterator.next();
			int[] data = known.get(key);
			
			int k = (new Integer(key)).intValue();
			int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;
			int i = data[0];
			int l = data[1];
			
			constraints.append( variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
			constraints.append( variables.getEnumeration("selected_orientation", new int[]{i, l}) + " 0\n");
			
			//System.out.println(k + "\t" + k_ + "\t" + i + "\t" + l);
		}
		
//		// set given tile
//		int set = (int)(Math.floor((136-1)/n) + 1)*(n+2) + (136-1)%n + 1;
//		constraints.append(variables.getEnumeration("selected_field", new int[]{(139-1), set}) + " 0\n");

		// the four first tiles can only be in the corners
		for (int i = 0; i < 4; i++){
			for (int k = 0; k < n*n; k++){
				if (k != 0 && k != n-1 && k != n*n-n && k != n*n-1){
					int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
					//System.out.println(k_);
					constraints.append(-variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
				}
			}
		}
		
		// some tiles can only be at the borders
		for (int i = 4; i < 4 + 4*(n-2); i++){
			for (int k = n; k < n*n - n; k++){
				if (!(k%n == 0 || k%n == n-1)){
					int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
					//System.out.println(k_);
					constraints.append(-variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
				}
			}
		}
		
		// some tiles can only be in the inside
		for (int i = 4 + 4*(n-2); i < n*n; i++){
			for (int k = 0; k < n; k++){
				int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
				constraints.append(-variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
			}
			for (int k = n*(n-1); k < n*n; k++){
				int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
				constraints.append(-variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
			}
			for (int k__ = 1; k__ < n-1; k__++){
				int k = k__*n;
				int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
				constraints.append(-variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
				
				k = k__*n + n - 1;
				k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;		// adapt variable to be in the middle of board
				constraints.append(-variables.getEnumeration("selected_field", new int[]{i, k_}) + " 0\n");
			}
		}
		
		// constraints for neighboring tiles
		if (verbose > -1)
			System.out.print("creating constraints ... ");
		for (int li = 1; li < n+1; li++){
			for (int lj = 1; lj < n+1; lj++){
				int l   = li*(n+2) + lj;
				int l_  = correct(l,n,n+2);
				int l__ = (int)(Math.floor(l_/n) + 1)*(n+2) + l_%n + 1;	// adapt variable to be in the middle of board

				//System.out.println(l + "\t<=>\t" + l_ + "\t<=>\t" + l__);
				if (hm.containsKey(l_ + "")){
				//System.out.println("jetzt");

				for (int j = 0; j < tile_colors.size(); j++){
					// first quadrant
					constraints.append(
							-variables.getEnumeration("realized_tile_on_board", new int[]{l  ,0,j}) + " " +
							 variables.getEnumeration("realized_tile_on_board", new int[]{l-(n+2),2,j}) + " 0\n"
							);	
					constraints.append(
							 variables.getEnumeration("realized_tile_on_board", new int[]{l  ,0,j}) + " " +
							-variables.getEnumeration("realized_tile_on_board", new int[]{l-(n+2),2,j}) + " 0\n"
							);
					// second quadrant
					constraints.append(
							-variables.getEnumeration("realized_tile_on_board", new int[]{l  ,1,j}) + " " +
							 variables.getEnumeration("realized_tile_on_board", new int[]{l+1,3,j}) + " 0\n"
							);	
					constraints.append(
							 variables.getEnumeration("realized_tile_on_board", new int[]{l  ,1,j}) + " " +
							-variables.getEnumeration("realized_tile_on_board", new int[]{l+1,3,j}) + " 0\n"
							);
					// third quadrant
					constraints.append(
							-variables.getEnumeration("realized_tile_on_board", new int[]{l  ,2,j}) + " " +
							 variables.getEnumeration("realized_tile_on_board", new int[]{l+(n+2),0,j}) + " 0\n"
							);	
					constraints.append(
							 variables.getEnumeration("realized_tile_on_board", new int[]{l  ,2,j}) + " " +
							-variables.getEnumeration("realized_tile_on_board", new int[]{l+(n+2),0,j}) + " 0\n"
							);
					// fourth quadrant
					constraints.append(
							-variables.getEnumeration("realized_tile_on_board", new int[]{l  ,3,j}) + " " +
							 variables.getEnumeration("realized_tile_on_board", new int[]{l-1,1,j}) + " 0\n"
							);	
					constraints.append(
							 variables.getEnumeration("realized_tile_on_board", new int[]{l  ,3,j}) + " " +
							-variables.getEnumeration("realized_tile_on_board", new int[]{l-1,1,j}) + " 0\n"
							);
				}
				}
			}
		}
		
		// hard coded borders
		int index = tile_colors.get("_");
		// horizontal first border
		for (int i = 1; i <= (n+2)-2; i++){
			for (int j = 0; j < 4; j++){
				for (int k = 0; k < tile_colors.size(); k++){
					int sign = -1;
					if (k == index)
						sign = 1;
					constraints.append(
							sign*variables.getEnumeration("realized_tile_on_board", new int[]{i,j,k}) + " 0\n"
							);
				}
			}
		}
		// horizontal second border
		for (int i = (n+2)*(n+2-1) + 1; i <= (n+2)*(n+2)-2; i++){
			for (int j = 0; j < 4; j++){
				for (int k = 0; k < tile_colors.size(); k++){
					int sign = -1;
					if (k == index)
						sign = 1;
					constraints.append(
							sign*variables.getEnumeration("realized_tile_on_board", new int[]{i,j,k}) + " 0\n"
							);
				}
			}
		}
		// vertical first border
		for (int i = 1; i <= (n+2)-2; i++){
			int i_ = (n+2)*i;
			for (int j = 0; j < 4; j++){
				for (int k = 0; k < tile_colors.size(); k++){
					int sign = -1;
					if (k == index)
						sign = 1;
					constraints.append(
							sign*variables.getEnumeration("realized_tile_on_board", new int[]{i_,j,k}) + " 0\n"
							);
				}
			}
		}
		// vertical second border
		for (int i = 2; i <= (n+2)-1; i++){
			int i_ = (n+2)*i - 1;
			for (int j = 0; j < 4; j++){
				for (int k = 0; k < tile_colors.size(); k++){
					int sign = -1;
					if (k == index)
						sign = 1;
					constraints.append(
							sign*variables.getEnumeration("realized_tile_on_board", new int[]{i_,j,k}) + " 0\n"
							);
				}
			}
		}		

		// copy selected field to realized_tile_on_field
		for (int i = 0; i < tiles.length; i++){						// tile
			for (int k = 0; k < n*n; k++){							// field
				for (int j = 0; j < 4; j++){						// quadrant
					for (int l = 0; l < tile_colors.size(); l++){	// color
						int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
						constraints.append(
								-variables.getEnumeration("selected_field", new int[]{i, k_}) + " " +
								-variables.getEnumeration("realized_tile_orientation" , new int[]{i, j, l}) + " " +
								 variables.getEnumeration("realized_tile_on_field"    , new int[]{i, k_, j, l}) + " 0\n"
								 );
						constraints.append(
								 variables.getEnumeration("selected_field", new int[]{i, k_}) + " " +
								-variables.getEnumeration("realized_tile_on_field"    , new int[]{i, k_, j, l}) + " 0\n"
								);
						constraints.append(
								 variables.getEnumeration("realized_tile_orientation", new int[]{i, j, l}) + " " +
								-variables.getEnumeration("realized_tile_on_field"   , new int[]{i, k_, j, l}) + " 0\n"
								);
					}
				}
			}
		}
		
		// copy realized_tile_on_fields to realized fields
		for (int l = 0; l < (n+2)*(n+2); l++){					// field
			for (int j = 0; j < 4; j++){						// quadrant
				for (int k = 0; k < tile_colors.size(); k ++){	// color
					for (int i = 0; i < tiles.length; i++){		// tile
						constraints.append(
								-variables.getEnumeration("realized_tile_on_field", new int[]{i, l, j, k}) + " " +
								 variables.getEnumeration("realized_tile_on_board", new int[]{l, j, k}) + " 0\n"
								);
					}
					String ret = "";
					for (int i = 0; i < tiles.length; i++)		// tile
						ret = ret + variables.getEnumeration("realized_tile_on_field", new int[]{i, l, j, k}) + " ";
					ret = ret + -variables.getEnumeration("realized_tile_on_board", new int[]{l, j, k}) + " 0\n";
					constraints.append(ret);
				}
			}
		}
		
		// select exactly one field each
		for (int i = 0; i < tiles.length; i++){
			String ret = "";
			for (int j = 0; j < n*n; j++){
				int j_ = (int)(Math.floor(j/n) + 1)*(n+2) + j%n + 1;	// adapt variable to be in the middle of board
				for (int k = j + 1; k < n*n; k++){
					int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
					constraints.append(-variables.getEnumeration("selected_field", new int[]{i,j_}) + " " + -variables.getEnumeration("selected_field", new int[]{i,k_}) + " 0\n");
				}
				ret = ret + variables.getEnumeration("selected_field", new int[]{i,j_}) + " ";
			}
			constraints.append(ret + "0\n");
		}
		
		// each field must be selected once
		for (int k = 0; k < n*n; k++){
			int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
			String ret = "";
			for (int i = 0; i < tiles.length; i++){
				for (int j = i + 1; j < tiles.length; j++){
					constraints.append(-variables.getEnumeration("selected_field", new int[]{i,k_}) + " " + -variables.getEnumeration("selected_field", new int[]{j,k_}) + " 0\n");
				}
				ret = ret + variables.getEnumeration("selected_field", new int[]{i, k_}) + " ";
			}
			constraints.append(ret + "0\n");
		}
		
		// hardcoded tiles
		for (int i = 0; i < tiles.length; i++){	// tile
			for (int j = 0; j < 4; j++){		// orientation
				for (int k = 0; k < 4; k++){	// quadrant
					index = tile_colors.get(tiles[i].charAt(k) + "");
					for (int l = 0; l < tile_colors.size(); l++){
						if (index == l)
							constraints.append( variables.getEnumeration("tile", new int[]{i, j, (k + j) % 4, l}) + " 0\n");
						else
							constraints.append(-variables.getEnumeration("tile", new int[]{i, j, (k + j) % 4, l}) + " 0\n");
					}
				}
			}
		}
		
		// each field must be selected once
		for (int k = 0; k < tiles.length; k++){
			String ret = "";
			for (int i = 0; i < 4; i++){
				for (int j = i + 1; j < 4; j++){
					constraints.append(-variables.getEnumeration("selected_orientation", new int[]{k, i}) + " " + -variables.getEnumeration("selected_orientation", new int[]{k, j}) + " 0\n");
				}
				ret = ret + variables.getEnumeration("selected_orientation", new int[]{k, i}) + " ";
			}
			constraints.append(ret + "0\n");
		}
		
		// copy selected orientation to help variable
		for (int i = 0; i < tiles.length; i++){	// tile
			for (int j = 0; j < 4; j++){		// orientation
				for (int k = 0; k < 4; k++){	// quadrant
					for (int l = 0; l < tile_colors.size(); l++){
						constraints.append(
								-variables.getEnumeration("selected_orientation", new int[]{i, j}) + " " +
								-variables.getEnumeration("tile", new int[]{i, j, k, l}) + " " +
								 variables.getEnumeration("help", new int[]{i, j, k, l}) + " 0\n"
								 );
						constraints.append(
								 variables.getEnumeration("selected_orientation", new int[]{i, j}) + " " +
								-variables.getEnumeration("help", new int[]{i, j, k, l}) + " 0\n"
								);
						constraints.append(
								 variables.getEnumeration("tile", new int[]{i, j, k, l}) + " " +
								-variables.getEnumeration("help", new int[]{i, j, k, l}) + " 0\n"
								);
					}
				}
			}
		}
		
		// copy help variables to realized tile
		for (int i = 0; i < tiles.length; i++){	// tile
			for (int k = 0; k < 4; k++){		// quadrant
				for (int l = 0; l < tile_colors.size(); l++){
					for (int j = 0; j < 4; j ++){
						constraints.append(
								-variables.getEnumeration("help", new int[]{i, j, k, l}) + " " +
								 variables.getEnumeration("realized_tile_orientation", new int[]{i, k, l}) + " 0\n"
								);
					}
					String ret = "";
					for (int j = 0; j < 4; j++)
						ret = ret + variables.getEnumeration("help", new int[]{i, j, k, l}) + " ";
					ret = ret + -variables.getEnumeration("realized_tile_orientation", new int[]{i, k, l}) + " 0\n";
					constraints.append(ret);
				}
			}
		}
		
		long stop_time2 = System.currentTimeMillis();
		if (verbose > -1)
			System.out.println("finished (" + (stop_time2 - stop_time) + " ms)");
		
		if (solution_file == null)
			solveWriteComputeRead(solver_path, input_file, output_file, seed, variables, constraints, verbose);
		else
			solveWriteRead(solution_file, variables, constraints, verbose);


		// ######################################################################
		//                            output result
		// ######################################################################
		
//		// print result
//		for (int i = 0; i < tiles.length; i++){	// tile
//			for (int j = 0; j < 4; j++){		// orientation
//				for (int k = 0; k < 4; k++){	// quadrant
//					for (int l = 0; l < tile_colors.size(); l++){
//						System.out.print(variables.variableToString("tile", new int[]{i, j, k, l}) + " ");
//					}
//					System.out.println();
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
//		
//		for (int i = 0; i < tiles.length; i++){	// tile
//			for (int j = 0; j < 4; j++)			// orientation
//				System.out.print(variables.variableToString("selected_orientation", new int[]{i, j}) + " ");
//			System.out.println();
//		}
//		
//		System.out.println("\n###\n");
//		
//		// print result
//		for (int i = 0; i < tiles.length; i++){	// tile
//			for (int j = 0; j < 4; j++){		// orientation
//				for (int k = 0; k < 4; k++){	// quadrant
//					for (int l = 0; l < tile_colors.size(); l++){
//						System.out.print(variables.variableToString("help", new int[]{i, j, k, l}) + " ");
//					}
//					System.out.println();
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
//		
//		System.out.println("\n###\n");
//		
//		for (int i = 0; i < tiles.length; i++){	// tile
//			for (int k = 0; k < 4; k++){		// quadrant
//				for (int l = 0; l < tile_colors.size(); l++){
//					System.out.print(variables.variableToString("realized_tile_orientation", new int[]{i, k, l}) + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
//		
//		System.out.println("\n###\n");
//		
//		for (int i = 0; i < tiles.length; i++){
//			for (int j = 0; j < (n+2)*(n+2); j++){
//				System.out.print(variables.variableToString("selected_field", new int[]{i, j}) + " ");
//			}
//			System.out.println();
//		}
//		
//		System.out.println("\n###\n");
//		
//		for (int i = 0; i < tiles.length; i++){
//			for (int j = 0; j < 4*4; j++){
//				for (int k = 0; k < 4; k++){
//					for (int l = 0; l < tile_colors.size(); l++){
//						System.out.print(variables.variableToString("realized_tile_on_field", new int[]{i, j, k, l}) + " ");
//					}
//					System.out.println();
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
//		
//		System.out.println("\n###\n");
//		
//		for (int l = 0; l < 4*4; l++){							// field
//			for (int k = 0; k < 4; k++){						// quadrant
//				for (int j = 0; j < tile_colors.size(); j++){	// color
//					System.out.print(variables.variableToString("realized_tile_on_board", new int[]{l, k, j}) + " ");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
//		
//		System.out.println("\n###\n");
//		
/*		for (int li = 0; li < (n+2); li++){							// board (vertical)
			for (int k = 0; k < 4; k++){						// quadrant
				for (int lj = 0; lj < (n+2); lj++){					// board (horizontal)
					int l = li*(n+2) + lj;
					for (int j = 0; j < tile_colors.size(); j++){	// color						
						System.out.print(variables.variableToString("realized_tile_on_board", new int[]{l, k, j}) + " ");
					}
					System.out.print("\t");
				}
				System.out.println();
			}
			System.out.println();
		}
		System.out.println();
*/		
		HashMap<String, int[]> ret = new HashMap<String, int[]>();
		iterator = known.keySet().iterator();
		while (iterator.hasNext()){
			String key = iterator.next();
			int[] data = known.get(key);
			ret.put(key, data);
		}
		
		// each field must be selected once
		//System.out.println("board\ttile");
		for (int k = 0; k < n*n; k++){
			if (hm.containsKey(k + "")){
				// which field
				int[] data = new int[2];
				String key = k +"";
				int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
				//System.out.print((k+1) + ":\t");
				for (int i = 0; i < tiles.length; i++){
					if (variables.getEvaluatedId("selected_field", new int[]{i, k_}) > 0){
						data[0] = i;	// which tile
						//System.out.print((i+1) + "\t");

						for (int l = 0; l < 4; l++)
							if (variables.getEvaluatedId("selected_orientation", new int[]{i, l}) > 0){
								data[1] = l;	// which orientation
								//System.out.println((l+1));
							}
					}
				}
				ret.put(key, data);
			}
		}
		
		return ret;
	}
}
