# Efficient Constrained K-center Clustering with Background Knowledge

  ## Data preprocessing 

  - Following the label for the dataset, we add CL and ML constraints on the pure dataset by label (which constructs the disjoint CL & ML constraints) to be input dataset for the main algorithm.
  - The input of the program can be pure constraint sets for CL & ML.

  ## Run the code 

  - Java version: 17.0.1
  - Cplex: IBM CPLEX2211 [https://www.ibm.com/products/ilog-cplex-optimization-studio](https://www.ibm.com/products/ilog-cplex-optimization-studio)
  - Run OuPut.java for the experimental evaluation
  - To simplify the code, we hard-code the following parameters: 

   ### Input [ML-CL-k-Center/code/addConstraints/OutPut.java]

    - set the file path at the _inputFilename_ (and the pure dataset needs to separate attribute values with commas);
    - input the parameter k: number of clusters, d: dimension, markPosition: label position

  ### Output [ML-CL-k-Center/code/addConstraints/OutPut.java]

    - set the file path at the _outputFilename_;

  #### Plot the output

  - Use the Cost, NMI, RI and runtime to calculate the agreement degree between an algorithm's clustering result and its labels. 
