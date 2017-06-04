# jlog
<div>
<a href="https://github.com/MichelML/jlog2"><img src="https://travis-ci.org/MichelML/jlog2.svg?branch=master"  alt='Build Status'></img></a>
</div>
Save Jira-issue related worklogs from the commandline as you do the actual work. Stop switching context for it. Stop relying on your memory to recall what you did.  
  
The goal is to save your worklog __as you do the actual work__. That being said, with jlog, you can thus only save worklogs for the current work day. 

## Installation  
  
Clone the repository locally.  
  
```
git clone https://github.com/MichelML/jlog.git  
```

Use an alias to point to the _jlog_ standalone jar in your `.bash_profile` file. 
  
```
alias jlog="java -jar <path to the jlog cloned repo>/target/jlog-0.1.0-SNAPSHOT-standalone.jar"
```

## Usage  

Valid commands for _jlog_ are:

``` 
   jlog -h                                            -     Prints the help menu to the console.
   jlog -o                                            -     Opens your jlog.txt file.

                       -- OR --

   jlog -b <timelog> <message in quotes>              -     Writes a worklog to the jlog.txt file, retrieving the issue-key from your branch.
   jlog <timelog> <issue key> <message in quotes>     -     Writes a worklog to the jlog.txt file using the provided information.
```  

## Examples  

Log a full day for a specific Jira issue  
```  
jlog 1d TEST-12 "write your comment message here"
```
  
Log a full day OR hours and minutes  
```bash
# valid commands
jlog 1d TEST-12 "write your comment message here"
jlog 2h30m TEST-12 "write your comment message here"
jlog 30m TEST-12 "write your comment message here"
jlog 2h TEST-12 "write your comment message here"

# invalid commands
jlog 1d1h TEST-12 "write your comment message here"
jlog 1d30m TEST-12 "write your comment message here"
```   
  
Log for a specific Jira issue retrieved from a repository branch
```bash
# while in your repository, on a branch having a name containing a Jira issue key
jlog -b 1h45m "write your comment message here"
```
