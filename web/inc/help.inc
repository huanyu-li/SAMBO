

<center> <h2> SAMBO Help Pages </h2> </center>
<hr noshade width="100%">

<ul> 	
	<li> <a class='helplink' href="#intro"> SAMBO Facts</a>
	
	<li> <a class='helplink' href="#start"> Upload Ontologies</a>
		
        <li> <a class='helplink' href="#alignment"> The Alignment Process </a>
        <ul> <li> <a class='helplink' href="#sug"> Aligning with Suggestions </a>
                  <ul>  <li> <a class='helplink' href="#matcher"> Alignment Strategies </a>
	          	<!-- <li> <a class='helplink' href="#relation"> Aligning Relations </a> -->
			<li> <a class='helplink' href="#concept"> Aligning Concepts </a>
                        <!-- <li> <a class='helplink' href="#suginfo"> Suggestion Information </a> -->          
	          </ul>
	
	     <li> <a class='helplink' href="#manual">Manual Aligning </a>
                  <ul>  <!--  <li> <a class='helplink' href="#manual_relation"> Aligning Relations </a>-->
			<li> <a class='helplink' href="#manual_concept"> Aligning Concepts </a>          
	          </ul>
	</ul>
		
	<li> <a class='helplink' href="#finish"> Alignments and Merging</a>
        <li> <a class='helplink' href="#checker"> Ontology Checker</a>
	<li> <a class='helplink' href="#cancel"> Cancel </a>
        <li> <a class='helplink' href="#help"> Get Help </a>
</ul>


<hr noshade width="100%">
<a name="intro"></a>
<h4>SAMBO Facts</h4>

<p> SAMBO is implemented based on our general alignment framework.<br><br>
<img src="img/framework.jpg" border="0" alt="Choose Upload Type Form" align="center">

<!-- <p>SAMBO is a system for aligning and merging biomedical ontologies. -->

<p>The alignment process is separated into two steps, aligning relations and aligning concepts.
 
<p> There are two alignment modes In "Manual Align", the user can specify equivalent terms or introduce is-a relationships between concepts themselves. 
The user also can have the system propose suggestions, and then accept or reject the suggestions in "Suggestion Align".  

<p>After the user finishes the alignment process the system can be asked to output the alignment in OWL file and 
construct a new ontology based on the alignments and source ontologies.

<p>SAMBO also provides a checker to avoid conflicts in an ontology, e.g. to detect unsatisfiable concepts and cycles, 
and to remove redundancy if so desired by the user.</p> 

The Process and Steps:<br>
<img src="img/step_start.gif" border="0"  align="center">

<p>

Visualize an ontology:<br>
The concepts in the ontologies are shown in is-a (i) and part-of (p) hierarchies.<br>
<img src="img/ontology.jpg" border="0"  align="center">

</p>

<p>

<a name="start"></a>
<h4> Upload Ontologies </h4>
<!-- <h4> Getting Started </h4>
<p>The user begins an aligning and merging process with starting a new task, or loading an old task.<br>
<img src="img/ontology_merge.jpg" border="0" alt="Ontology Merge" >

<p><b>Load An Existing Task</b><br>
Load an old task, which is saved as a XML file.<br>
<img src="img/load_merge.jpg" border="0" alt="Load" >

<p><b>Start A New Task</b><br>
Upload the ontologies. 

<p>
Select the ontology language. The ontologies can be represented in DAML+OIL and OWL. The default value is OWL.
An ontology can be uploaded from local disk, internet, or can be provided by the server.

<br>
Choose Ontology Language:<br>
<img src="img/choose_lan.jpg" border="0" alt="Choose Upload Type Form" align="center">
<br>
-->
<br>
Choose Upload Type:<br>
<img src="img/choose_type.jpg" border="0" alt="Choose Upload Type Form" align="center">
<br>

<ul> 	<li><b>Uploading from internet:</b> <br>
	Give the URI of the ontology file, e.g. http://www.myserver.edu/ontology.owl

	<li><b>Uploading from local disk:</b> <br>
	Give the local path to the ontology file.

	<li><b>Using an ontology on the server:</b> <br>
	Choose an ontology from the drop-down list.
</ul>

<!--
<a name="uploading"></a>
<br>
Choose Ontologies: <br>
-->

<img src="img/choose_file.jpg" border="0" alt="Choose Files Form" align="center">
<br>

You can assign a name to the result ontology in the 'New Ontology' box.<br>
<br><br><br>

<a name="alignment"></a>
<h4> The Alignment Process </h4>

<a name="sug"></a>
<em>Aligning with Suggestions</em>
<p>
<a name="matcher"></a>
<b>Alignment Strategies</b>
<p>
<img src="img/choose_algorithm.jpg" border="0" alt="Choose Matching Algorithms" align="center">

<p>Currently we implement the following matchers. All the matchers compute the similarity value between 0 and 1: 
<ul>	<li><b>TermBasic</b> <br>
	    includes algorithms based on the name and synonyms of the terms
	<li><b>TermWN</b> <br>
            using WordNet in TermBasic
        <li><b>UMLSKSearch</b> <br>
	    is a matcher using UMLS (Unified Medical Language System).
        <li><b>Hierarchy</b> <br>
	    is an algorithm based on the is-a and part-of hierarchies of the ontologies
        <li><b>BayesLearning</b> <br>
	    is an algorithm based on biomedical literature related to concepts
</ul> 

We also implement a combination algorithm which combines the similarity values from several mathcers using weights. 
The filtering algorithm is to filter out the suggestions with the similarity value lower than a threshold.

<!-- The alignment suggestions are generated by the different matchers or their combinations. 
They are based on the computation of similarity values between the terms in the ontologies.

<p> The user can choose one or multiple matchers (check boxes). Weights are assigned to each matcher. 
The threshold is the value for filtering the generated suggestions. Suggestions with a similarity value
equal or higher than the threshold will be presented to the user.

-->

<!--
<p>
<a name="relation"></a>
<b> Aligning Relations </b>
<p>
When skipping the relations with same name, a new name is required for one of the relations<br>
<img src="img/sugmerge_slots1.jpg" border="0" alt="Select Slot Suggestion Form 1" align="center">
<br><br>
When aligning the suggestion with different types:<br>
<img src="img/sugmerge_slots2.jpg" border="0" alt="Select Slot Suggestion Form 2" align="center">

<br>

<ul> 	<li><b>Undo</b> <br>
            Undo the previous action.
        <li><b>Align Relation</b> <br>
            Accept the suggestion, i.e. there is an equivalence relation between the two relations. 
            If the relations have different types, the user chooses one type for them.
  	<li><b>Skip to Next</b> <br>
  	    Reject the suggestion. Each term has a unique identifier in an ontology. 
            So if the two relations have the same name, the user should give a new name for one of the relations.
       <li><b>Manual Align</b> <br>
            Change to the manual mode.
</ul>

-->


<p>
<a name="concept"></a>
<b> Aligning Concepts </b>

<p>Concept Suggestions: <br>
<img src="img/sugmerge_classes.jpg" border="0" alt="Class Suggestion Form" align="center">
<br>

<ul> 	
	<li><b>Equiv. Concepts</b> <br>
	    Accept the suggestion, i.e. there is an equivalence relation between the two concepts.
	<li><b>Sub-Concept</b> <br>
	    Accept the suggestion, i.e. there is an is-a relation between the two concepts. 
            The first concept is a sub-concept of the second one.
        <li><b>Super-Concept</b> <br>
	    Accept the suggestion, i.e. there is an is-a relation between the two concepts. 
            The first concept is a super-concept of the second one.<br><br>
        
        <li><b>Undo</b> <br>
	    Undo the previous action.
	<li><b>Skip to Next</b> <br>
	    Reject the suggestion.<br><br>
        
        <li><b>Align Remaining</b> <br>
	    Accept all the remaining suggestions, i.e. there is equivalence relations
            between the concepts in the suggestions.<br><br>
       
        <li><b>Manual Align</b> <br>
            Move to the Manual mode.<br><br>
<!--</ul>

 <a name="suginfo"></a>
<b> Suggestion Information </b>

<ul> -->
	<li><b>Remaining Suggestions</b> <br>
            Click "Remaining Suggestions" to receive an overview  of all the remaining suggestions.
            <br>
            <img src="img/remaining_sugs.jpg" border="0" ><br><br>
	<li><b>History</b> <br>
	    Click "History" to receive an overview of the concepts already aligned.
            <br>
            <img src="img/previously_merge.jpg" border="0" ><br><br>

       <li><b>Warning</b> <br>
            The conflicts introduced by the alignments will be given in the warning box.
</ul>


<a name="manual"></a>


<em> Manual Aligning </em>

<p> The user specifies the equivalence relations or introduces is-a relations between the concepts 
from the two source ontologies. 

<!--
<p>
<a name="manual_relation"></a>
<b> Aligning Relations </b>
<p>The user specifies the equivalence relations manually. </p>

<img src="img/manual_slots.jpg" border="0" alt="Manaual Slot Form" align="center">
<br>

<ul>	<li><b>Undo</b> <br>
	    Undo the previous action.
	<li><b>Align Relations</b> <br>
	    Specify the equivalence relation between the selected relations. 
	<li><b>Suggestion Align</b> <br>
            Change to the suggestion mode.
</ul>

-->

<p>
<a name="manual_concept"></a>
<b> Aligning Concepts </b>
<p>The source ontologies are represented as trees which can be expanded or shrunk.
The complete ontologies can be viewed by clicking on the ontology names.

<br>
<img src="img/manual_classes.jpg" border="0" alt="Manual Class Form" align="center">
<br>


<ul>	
       <li><b>Equiv. Concepts</b> <br>
	    Accept the suggestion, i.e. there is an equivalence relation between the two concepts.
	<li><b>Sub-Concept</b> <br>
	    Accept the suggestion, i.e. there is an is-a relation between the two concepts. 
            The first concept is a sub-concept of the second one.
        <li><b>Super-Concept</b> <br>
	    Accept the suggestion, i.e. there is an is-a relation between the two concepts. 
            The first concept is a super-concept of the second one.<br><br>
        
        <li><b>Undo</b> <br>
	    Undo the previous action.
	<li><b>Skip to Next</b> <br>
	    Reject the suggestion.<br><br>

	<li><b>Suggestion Align</b> <br>
	    Move to the suggestion mode.<br><br>

	<li><b>History</b> <br>
	    Click "History" to receive an overview of the concepts already aligned.<br><br>

       <li><b>Warning</b> <br>
            The conflicts introduced by the alignments will be given in the warning box.
</ul>
	

<a name="finish"></a>
<h4> Alignment and Merging </h4>

<p>After the alignment process,</p>

<ul> 	
        <li><b>The Alignment in OWL file</b> <br>
            View the alignments in an OWL file.
       <li><b>The Merged Ontology in OWL file</b> <br>
	    View the merged ontology in a OWL file.
       <li><b>Browse the Merged Ontology</b> <br>
	    Overview the merged ontology.	
       <li><b>Reasoning the Merged Ontology</b> <br>
	    Connect to a reasoner available on the server and provide reasoning services for the merged ontology.
</ul>

<br>
<a name="checker"></a>
<h4> Ontology Checker </h4>
<!--The checker is provided to avoid to detect unsatisfiable concepts, 
cycles, and to compute new subsumptions, and remove redundancy. -->
The system accepts the DIG interface reasoners. In SAMBO the Racer provides the reasoning service.
<br><br>
<li><b>Start Reasoning :</b>
<p>
Set the upload type of the ontology,
and set a DIG reasoner which provides the reasoning services.<br>

<img src="img/checker_start.jpg" border="0" alt="choose the reasoner" align="center">

<br><br>
<li><b>Result Report :</b>
<p>The cycles, inconsistent concepts and computed subsumptions will be presented.
<p>For example, suppose A, B and C are concepts in the ontology. C is a direct subclass of B, B is a direct subclass of A;
<li> The relation "A is a subclass of C" will cause a cycle.
<li> The axim "C is disjont with A" will cause an inconsistency.
<li> The relation "C is a direct subclass of A" will cause a subsumption.

<!-- In "The classified Ontology" all the
redundancies have been removed.<br>

<img src="img/checker_result.jpg" border="0" alt="reasoning result report" align="center">
-->

<br>
<a name="cancel"></a>
<h4> Cancel </h4>

<img src="img/exit.gif" border="0" alt="Cancel Merge Button" >
The going task can be cancelled by clicking this icon.

<!--
<p>The aligning and merging task can be saved as an XML file.<br>
<img src="img/save_merge.jpg" border="0" alt="Save Merge" ><br>
<p>Then this session will be invalidated.</p>

<img src="img/savemerge_file.jpg" border="0" alt="The XML File" >
-->

<br>
<a name="help"></a>
<h4> Get Help </h4>


<img src="img/help.gif" border="0" alt="Get Help" >
Get help information by clicking this icon

<hr noshade width="100%">
