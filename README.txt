SAMBO-WebApp-Session is an extended version of SAMBO [1] with the following features : 
1) Session-based alignment process. The user starts an alignment process for two ontologies by initiating a session. The session can be paused and saved by the user in the middle of the alignment process. And the saved session can be loaded by the user to continue with the alignment process. Besides, one user can create multiple sessions.
2) Recommending alignment strategies [2]. Before aligning concepts, the user can use the feature of recommendation to test several predefined alignment strategies (i.e. the combination of matchers, the weights of matchers and the threshold for filtering mapping suggestions). The test results will be shown to the user, who decides which one is better to use.
3) PRAs algorithms [3]. Whenever a session is locked in the middle of an alignment process, the user can choose to run the PRA algorithm. Using the mapping suggestions already processed by the user as PRA mappings, the PRA algorithm refines the mapping suggestions not processed by the user. The refined mapping suggestions will be shown to the user when the saved session is reloaded.  


About the project folder:
1) File "dbssambo.sql" :
   Before system runs, this file should be used to initialize the database tables for the support of sessions and recommendation algorithm. 

------------------------------------------------------------------------------------------------------
Reference  

[1] Lambrix P, Tan H, SAMBO - A System for Aligning and Merging Biomedical Ontologies, Journal of Web Semantics, Special issue on Semantic Web for the Life Sciences, 4(3):196-206, 2006.  
[2] Lambrix P, Liu Q, Using partial reference alignments to align ontologies, Proceedings of the 6th European Semantic Web Conference - ESWC09, LNCS 5554, 188-202, Heraklion, Greece, 2009. @ Springer-Verlag.
[3] Tan H, Lambrix P, A method for recommending ontology alignment strategies, Proceedings of the 6th International Semantic Web Conference and 2nd Asian Semantic Web Conference - ISWC / ASWC 07, LNCS 4825, 494-507, Busan, Korea, 2007. @ Springer-Verlag