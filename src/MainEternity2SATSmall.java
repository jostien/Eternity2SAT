import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import jsatbox.miscellaneous.VariablesContainer;
import jsatbox.parsers.SATsolver;

public class MainEternity2SATSmall{
	public static Random r = new Random();
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		StringBuffer constraints = new StringBuffer();
		VariablesContainer variables = new VariablesContainer();
		
		int n = 16;
		
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
		
		// output colors and corresponding index (only for debugging)
		colors = new String[tile_colors.size()];
		tile_colors.keySet().toArray(colors);
		for (int i = 0; i < colors.length; i++)
			System.out.println(colors[i] + "\t" + tile_colors.get(colors[i]));
		
		// ######################################################################
		//                          create variables
		// ######################################################################
		
		long start_time = System.currentTimeMillis();

		System.out.print("creating variables ... ");
		
		// for hardcoded numbers
		variables.add("number", new int[]{
				tiles.length,	// tile
				8				// one byte
		});
		
		// for selected numbers
		variables.add("selected_number", new int[]{
				tiles.length,	// tile
				8				// one byte
		});
		
		// for helping 2
		variables.add("help2", new int[]{
				tiles.length,	// tile
				tiles.length,	// tile^2
				8				// one byte
		});
		
		// for helping 3
		variables.add("help3", new int[]{
				tiles.length,
				tiles.length,
				8
		});
		
		// for all tiles with all orientations
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
		
		// for the chosen orientation and tiles on the board
		variables.add("realized_tile_on_board", new int[]{
				(n+2)*(n+2),		// field
				4,					// quadrant
				tile_colors.size()	// color
				});
		
		long stop_time = System.currentTimeMillis();
		System.out.println("finished (" + (stop_time - start_time) + " ms)");
		
		// ######################################################################
		//                         create constraints
		// ######################################################################

		System.out.print("creating constraints ... ");
		
//		// set given tile
//		int set = (int)(Math.floor((136-1)/n) + 1)*(n+2) + (136-1)%n + 1;
//		constraints.append(variables.getEnumeration("selected_field", new int[]{(139-1), set}) + " 0\n");

		// hardcoded bytes (numbers)
		for (int i = 0; i < tiles.length; i++){
			String coding = Integer.toBinaryString(i);
			while (coding.length() < 8)
				coding = "0" + coding;
			for (int k = coding.length() - 1; k > -1; k--){
				if (coding.charAt(k) == '0')
					constraints.append(-variables.getEnumeration("number", new int[]{i, 8 - (k+1)}) + " 0\n");
				else
					constraints.append( variables.getEnumeration("number", new int[]{i, 8 - (k+1)}) + " 0\n");
			}
		}
		
		// selected bytes (numbers)
		for (int k = 0; k < 8; k++){
			for (int i = 0;  i < tiles.length; i++){
				for (int j = i+1; j < tiles.length; j++){
					constraints.append(-variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
										variables.getEnumeration("selected_number", new int[]{j, k}) + " " +
										variables.getEnumeration("help2",           new int[]{i,j,k}) + " 0\n");
					constraints.append( variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
									   -variables.getEnumeration("selected_number", new int[]{j, k}) + " " +
									    variables.getEnumeration("help2",           new int[]{i,j,k}) + " 0\n");
					constraints.append( variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
									    variables.getEnumeration("selected_number", new int[]{j, k}) + " " +
									   -variables.getEnumeration("help2",           new int[]{i,j,k}) + " 0\n");
					constraints.append(-variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
									   -variables.getEnumeration("selected_number", new int[]{j, k}) + " " +
									   -variables.getEnumeration("help2",           new int[]{i,j,k}) + " 0\n");
				}
			}
		}
		for (int i = 0;  i < tiles.length; i++){
			for (int j = i+1; j < tiles.length; j++){
				String ret = "";
				for (int k = 0; k < 8; k++)
					ret = ret + variables.getEnumeration("help2", new int[]{i,j,k}) + " ";
				constraints.append(ret + "0\n");
			}
		}

		// mark fields for tiles corresponding to selected number
		for (int i = 0; i < tiles.length; i++){
			for (int j = 0; j < tiles.length; j++){
				for (int k = 0; k < 8; k++){
					constraints.append(-variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
										variables.getEnumeration("number",          new int[]{j, k}) + " " +
									   -variables.getEnumeration("help3",           new int[]{i, j, k}) + " 0\n");
					constraints.append( variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
									   -variables.getEnumeration("number",          new int[]{j, k}) + " " +
									   -variables.getEnumeration("help3",           new int[]{i, j, k}) + " 0\n");
					constraints.append( variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
										variables.getEnumeration("number",          new int[]{j, k}) + " " +
									    variables.getEnumeration("help3",           new int[]{i, j, k}) + " 0\n");
					constraints.append(-variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
									   -variables.getEnumeration("number",          new int[]{j, k}) + " " +
									    variables.getEnumeration("help3",           new int[]{i, j, k}) + " 0\n");
				}
			}
		}

		// mark fields for tiles corresponding to selected number
		for (int i = 0; i < tiles.length; i++){
			for (int j = 0; j < tiles.length; j++){
				int j_ = (int)(Math.floor(j/n) + 1)*(n+2) + j%n + 1;	// adapt variable to be in the middle of board
				String ret = "";
				for (int k = 0; k < 8; k++)
					ret = ret + -variables.getEnumeration("help3", new int[]{i, j, k}) + " ";
				constraints.append(ret + variables.getEnumeration("selected_field", new int[]{i, j_}) + " 0\n");
				
				for (int k = 0; k < 8; k++)
					constraints.append( variables.getEnumeration("help3",          new int[]{i, j, k}) + " " +
							           -variables.getEnumeration("selected_field", new int[]{i, j_}) + " 0\n");
			}
		}
		
		// mark fields for tiles corresponding to selected number
		for (int i = 0; i < tiles.length; i++){
			for (int j = 0; j < tiles.length; j++){
				int j_ = (int)(Math.floor(j/n) + 1)*(n+2) + j%n + 1;	// adapt variable to be in the middle of board
				for (int k = 0; k < 8; k++){
					constraints.append(-variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
										variables.getEnumeration("number",          new int[]{j, k}) + " " +
									   -variables.getEnumeration("selected_field",  new int[]{i, j_}) + " 0\n");
					constraints.append( variables.getEnumeration("selected_number", new int[]{i, k}) + " " +
									   -variables.getEnumeration("number",          new int[]{j, k}) + " " +
									   -variables.getEnumeration("selected_field",  new int[]{i, j_}) + " 0\n");
				}
			}
		}
		
		// copy selected tile to board
		for (int i = 0; i < tiles.length; i++){
			for (int j = 0; j < tiles.length; j++){
				int j_ = (int)(Math.floor(j/n) + 1)*(n+2) + j%n + 1;	// adapt variable to be in the middle of board
				for (int l = 0; l < 4; l++){
					for (int k = 0; k < colors.length; k++){
						constraints.append(-variables.getEnumeration("realized_tile_orientation", new int[]{i ,l,k}) + " " +
											variables.getEnumeration("realized_tile_on_board",    new int[]{j_,l,k}) + " " +
										   -variables.getEnumeration("selected_field",            new int[]{i, j_})  + " 0\n");
						constraints.append( variables.getEnumeration("realized_tile_orientation", new int[]{i ,l,k}) + " " +
										   -variables.getEnumeration("realized_tile_on_board",    new int[]{j_,l,k}) + " " +
										   -variables.getEnumeration("selected_field",            new int[]{i, j_})  + " 0\n");
					}
				}
			}
		}
		
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
		
		String[] to_keep = new String[]{"19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
										"37","52",
										"55","70",
										"73","88",
										"91","106",
										"109","124",
										"127","142",
										"145","160",
										"163","178",
										"181","196",
										"199","214",
										"217","232",
										"235","250",
										"253","268",
										"271","286",
										"289","290","291","292","293","294","295","296","297","298","299","300","301","302","303","304"};

		HashMap<String,String> hm = new HashMap<String,String>();
		putIntoHash(hm, to_keep);
		
		// constraints for neighboring tiles
		for (int li = 1; li < n-1; li++){
			for (int lj = 1; lj < n+1; lj++){
				int l = li*(n+2) + lj;
				System.out.println(l);
				if (hm.containsKey(l + "")){
				System.out.println("jetzt");
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
		System.out.println("finished (" + (stop_time2 - stop_time) + " ms)");
		
		// solve constraints
		//String solver_path = "/home/jostie/tools/minisat/core/minisat_static";
		//String solver_path = "/home/jostie/workspace/SATlotyping/satsolver/minisat/simp/minisat_static";
		Integer seed = null;
		int seed_ = r.nextInt();
		if (seed != null)
			seed_ = seed.intValue();
		
		int verbose = 0;
		String solver_path = "/home/jostie/tools/cryptominisat/build/cryptominisat4.sh";
		SATsolver ss = new SATsolver(variables, solver_path, "/home/jostie/Desktop/sum_test.cnf", "/home/jostie/Desktop/out", seed_);
		if (verbose > -1)
			System.out.print("executing " + ss.getCmd() + " ... ");
		stop_time2 = System.currentTimeMillis();
		String status = ss.execute(constraints, verbose > 0);
		long stop_time3 = System.currentTimeMillis();
		if (verbose > -1)
			System.out.println("finished (" + (stop_time3 - stop_time2) + " ms)");

		// ######################################################################
		//                            output result
		// ######################################################################
		
		// print result
		for (int i = 0; i < tiles.length; i++){
			for (int k = 0; k < 8; k++){
				System.out.print(variables.variableToString("selected_number", new int[]{i, k}) + " ");
			}
			System.out.println();
		}
		
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
		
		System.out.println("\n###\n");
		
		for (int i = 0; i < tiles.length; i++){
			for (int j = 0; j < (n+2)*(n+2); j++){
				System.out.print(variables.variableToString("selected_field", new int[]{i, j}) + " ");
			}
			System.out.println();
		}
		
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
		for (int li = 0; li < (n+2); li++){							// board (vertical)
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
		
		// each field must be selected once
		System.out.println("board\ttile");
		for (int k = 0; k < n*n; k++){
			int k_ = (int)(Math.floor(k/n) + 1)*(n+2) + k%n + 1;	// adapt variable to be in the middle of board
			System.out.print((k+1) + ":\t");
			for (int i = 0; i < tiles.length; i++){
				if (variables.getEvaluatedId("selected_field", new int[]{i, k_}) > 0)
					System.out.println((i+1));
			}
		}
	}
	
	public static void putIntoHash(HashMap<String,String> hm, String[] indices){
		for (int i = 0; i < indices.length; i++)
			hm.put(indices[i], indices[i]);
	}
}
