SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for predefinedstrategies
-- ----------------------------
DROP TABLE IF EXISTS `predefinedstrategies`;
CREATE TABLE `predefinedstrategies` (
  `id` int(11) NOT NULL auto_increment,
  `matchers` varchar(100) NOT NULL,
  `submatchers` varchar(100) NOT NULL,
  `weights` varchar(50) NOT NULL,
  `subweights` varchar(11) NOT NULL,
  `threshold` float NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for savedpredefinedstrategies 
-- ----------------------------
DROP TABLE IF EXISTS `savedpredefinedstrategies`;
CREATE TABLE `savedpredefinedstrategies` (
  `id` int(11) NOT NULL auto_increment,
  `predefinedstrategyid` int(11) NOT NULL,
  `ontology1` varchar(200) NOT NULL,
  `ontology2` varchar(200) NOT NULL,
  `matchers` varchar(200) NOT NULL,
  `weights` varchar(200) NOT NULL,
  `combination` varchar(200) NOT NULL,
  `threshold` varchar(200) NOT NULL,
  `fmeasure` double NOT NULL,
  `quality` double NOT NULL,
  `precision1` double NOT NULL,
  `recall` double NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for savedpredefinedstrategiessuggestions
-- ----------------------------
DROP TABLE IF EXISTS `savedpredefinedstrategiessuggestions`;
CREATE TABLE `savedpredefinedstrategiessuggestions` (
  `id` int(11) NOT NULL auto_increment,
  `savedpredefinedstrategiesid` int(11) NOT NULL,
  `suggestionsXML` longtext NOT NULL,
  `suggestionsVector` longtext,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL auto_increment,
  `email` varchar(50) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- -----------------------------------
-- Table structure for usersessions
-- -----------------------------------
DROP TABLE IF EXISTS `usersessions`;
CREATE TABLE `usersessions` (
  `id` int(11) NOT NULL auto_increment,
  `userid` int(11) NOT NULL,
  `email` varchar(50) NOT NULL,
  `ontology1` varchar(50) NOT NULL,
  `ontology2` varchar(50) NOT NULL,
  `color1` varchar(50) NOT NULL,
  `color2` varchar(50) NOT NULL,
  `matchername0` varchar(50) default NULL,
  `matchervalue0` varchar(50) default NULL,
  `weightvalue0` double default NULL,
  `matchername1` varchar(50) default NULL,
  `matchervalue1` varchar(50) default NULL,
  `weightvalue1` double default NULL,
  `matchername2` varchar(50) default NULL,
  `matchervalue2` varchar(50) default NULL,
  `weightvalue2` double default NULL,
  `matchername3` varchar(50) default NULL,
  `matchervalue3` varchar(50) default NULL,
  `weightvalue3` double default NULL,
  `matchername4` varchar(50) default NULL,
  `matchervalue4` varchar(50) default NULL,
  `weightvalue4` double default NULL,
  `matchername5` varchar(50) default NULL,
  `matchervalue5` varchar(50) default NULL,
  `weightvalue5` double default NULL,
  `matchername6` varchar(50) default NULL,
  `matchervalue6` varchar(50) default NULL,
  `weightvalue6` double default NULL,
  `matchername7` varchar(50) default NULL,
  `matchervalue7` varchar(50) default NULL,
  `weightvalue7` double default NULL,
  `matchername8` varchar(50) default NULL,
  `matchervalue8` varchar(50) default NULL,
  `weightvalue8` double default NULL,
  `matchername9` varchar(50) default NULL,
  `matchervalue9` varchar(50) default NULL,
  `weightvalue9` double default NULL,
  `thresholdvalue` double NOT NULL,
  `session_type` varchar(50) NOT NULL,
  `sid` varchar(50) NOT NULL,
  `step` tinyint(4) NOT NULL,
  `is_finalized` int(11) NOT NULL,
  `user_xml` longtext,
  `user_historylist_xml` longtext,
  `user_relations_historylist_xml` longtext,
  `user_suggestions_list_xml` longtext,
  `user_temp_xml` longtext,
  `creation_time` datetime NOT NULL,
  `last_accessed_time` datetime NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `predefinedstrategies` VALUES ('1', 'WL', '', '1.0', '', '0.6');
INSERT INTO `predefinedstrategies` VALUES ('2', 'WL', '', '1.0', '', '0.4');
INSERT INTO `predefinedstrategies` VALUES ('3', 'EditDistance;NGram', '', '1.0;2.0', '', '0.4');
INSERT INTO `predefinedstrategies` VALUES ('4', 'EditDistance;NGram', '', '1.0;1.0', '', '0.6');
INSERT INTO `predefinedstrategies` VALUES ('5', 'TermBasic;TermWN', '', '1.0;1.0', '', '0.6');

INSERT INTO `users` VALUES ('1', 'tester');


ALTER TABLE usersessions AUTO_INCREMENT = 1;








-- -----------------------------------------
-- Table structure for  mappablesuggestions
-- -----------------------------------------
DROP TABLE IF EXISTS `mappablesuggestions`;
CREATE TABLE `mappablesuggestions` (
  `ontologies` varchar(500) default NULL,
  `suggestion` varchar(500) default NULL,
      PRIMARY KEY  (ontologies, suggestion)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------------
-- Table structure for savesimvalues
-- ----------------------------------
DROP TABLE IF EXISTS `savesimvalues`;
CREATE TABLE `savesimvalues` (
  `ontologies` varchar(500) default NULL,
  `concept1` varchar(500) default NULL,
  `concept2` varchar(500) default NULL,
    PRIMARY KEY  (ontologies, concept1, concept2)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 
    
    
-- ------------------------------------------
-- Table structure for resultsforcombination 
-- ------------------------------------------
DROP TABLE IF EXISTS `resultsforcombination`;
CREATE TABLE `resultsforcombination` (
  `ontologies` varchar(200) NOT NULL,
  `combination` varchar(200) NOT NULL,
  `columnnameinsimvaluetable` varchar(200) NOT NULL,
  `isresultavailable` varchar(200) NOT NULL,
   PRIMARY KEY  (ontologies,combination)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ---------------------------------------
-- Table structure for evaluatestrategies
-- ---------------------------------------
DROP TABLE IF EXISTS `evaluatestrategies`;
CREATE TABLE `evaluatestrategies` (
    `matcher` varchar(200) NOT NULL,
  `weight` varchar(200) NOT NULL,
  `combination` varchar(200) NOT NULL,
  `threshold` varchar(200) NOT NULL,
   `a` double NOT NULL,
   `b` double NOT NULL,
   `c` double NOT NULL,
   `d` double NOT NULL,
   `recallPlus` double NOT NULL,
   `precisionPlus` double NOT NULL,
   `recallMinus` double NOT NULL,
   `precisionMinus` double NOT NULL,
   `score1` double NOT NULL,
   `score2` double NOT NULL,
   PRIMARY KEY  (matcher,weight,combination,threshold)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- ------------------------------------------
-- Table structure for recommendationmethod2
-- ------------------------------------------
DROP TABLE IF EXISTS `recommendationmethod2`;
CREATE TABLE `recommendationmethod2` (
 `ontologies` varchar(200) NOT NULL,
    `matcher` varchar(200) NOT NULL,
  `weight` varchar(200) NOT NULL,
  `combination` varchar(200) NOT NULL,
  `threshold` varchar(200) NOT NULL,
   `a` double NOT NULL,
   `b` double NOT NULL,
   `c` double NOT NULL,
   `d` double NOT NULL,
   `e` double NOT NULL,
   `recallCorrect` double NOT NULL,
   `precisionCorrect` double NOT NULL,
   `fmeasureCorrect` double NOT NULL,
   `recallWrong` double NOT NULL,
   `precisionWrong` double NOT NULL,
   `fmeasureWrong` double NOT NULL,
   `score1` double NOT NULL,
   `score2` double NOT NULL,
   PRIMARY KEY  (ontologies,matcher,weight,combination,threshold)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------
-- Table structure for recommendationmethod3
-- ------------------------------------------
DROP TABLE IF EXISTS `recommendationmethod3`;
CREATE TABLE `recommendationmethod3` (
 `ontologies` varchar(200) NOT NULL,
    `matcher` varchar(200) NOT NULL,
  `weight` varchar(200) NOT NULL,
  `combination` varchar(200) NOT NULL,
  `threshold` varchar(200) NOT NULL,
   `a` double NOT NULL,
   `b` double NOT NULL,
   `c` double NOT NULL,
   `d` double NOT NULL,   
   `recallCorrect` double NOT NULL,
   `precisionCorrect` double NOT NULL,
   `fmeasureCorrect` double NOT NULL,
   `recallWrong` double NOT NULL,
   `precisionWrong` double NOT NULL,
   `fmeasureWrong` double NOT NULL,
   `score1` double NOT NULL,
   `score2` double NOT NULL,
   PRIMARY KEY  (ontologies,matcher,weight,combination,threshold)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ------------------------------------------
-- Table structure for recommendationmethod1
-- ------------------------------------------
DROP TABLE IF EXISTS `recommendationmethod1`;
CREATE TABLE `recommendationmethod1` (
 `ontologies` varchar(200) NOT NULL,
    `matcher` varchar(200) NOT NULL,
  `weight` varchar(200) NOT NULL,
  `combination` varchar(200) NOT NULL,
  `threshold` varchar(200) NOT NULL,
   `a` double NOT NULL,
   `b` double NOT NULL,
   `c` double NOT NULL,
   `d` double NOT NULL,   
   `recallCorrect` double NOT NULL,
   `precisionCorrect` double NOT NULL,
   `fmeasureCorrect` double NOT NULL,
   `recallWrong` double NOT NULL,
   `precisionWrong` double NOT NULL,
   `fmeasureWrong` double NOT NULL,
   `score1` double NOT NULL,
   `score2` double NOT NULL,
   PRIMARY KEY  (ontologies,matcher,weight,combination,threshold)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- -------------------------------
-- Table structure for allhistory
-- -------------------------------
DROP TABLE IF EXISTS `allhistory`;
CREATE TABLE `allhistory` (
  `concepts` varchar(500) default NULL,
  `decision` int(11) default NULL,
    PRIMARY KEY  (concepts)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




-- ------------------------------------
-- Table structure for filterPRABased
-- ------------------------------------
DROP TABLE IF EXISTS `filterPRABased`;
CREATE TABLE `filterPRABased` (
  `conceptID1` varchar(500) NOT NULL,
  `conceptID2` varchar(500) NOT NULL,
  `label1` varchar(500) NOT NULL,
  `label2` varchar(500) NOT NULL,
   `filter` varchar(500) NOT NULL,
   PRIMARY KEY  (conceptID1,conceptID2,filter)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- ---------------------------------------
-- Table structure for bioportal synonyms
-- ---------------------------------------
DROP TABLE IF EXISTS `synonyms`;
CREATE TABLE `synonyms` (
  `term` varchar(500) default NULL,
  `synonyms` varchar(500) default NULL,
      PRIMARY KEY  (term)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------------------
-- Table structure for  bioportal mappings
-- ----------------------------------------
DROP TABLE IF EXISTS `mappings`;
CREATE TABLE `mappings` (
  `term` varchar(500) default NULL,
  `mappings` longtext default NULL,
      PRIMARY KEY  (term)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ---------------------------------------
-- Table structure for  bioportal parents
-- ---------------------------------------
DROP TABLE IF EXISTS `parents`;
CREATE TABLE `parents` (
  `term` varchar(500) default NULL,
  `parents` varchar(500) default NULL,
      PRIMARY KEY  (term)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


-- --------------------------------
-- Table structure for  google url
-- --------------------------------
DROP TABLE IF EXISTS `googleurl`;
CREATE TABLE `googleurl` (
  `term` varchar(500) default NULL,
  `url` longtext default NULL,
      PRIMARY KEY  (term)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

