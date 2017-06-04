# jlog
<div>
<a href="https://github.com/MichelML/jlog2"><img src="https://travis-ci.org/MichelML/jlog2.svg?branch=master"  alt='Build Status'></img></a>
</div>
log time spent on jira issues from the commandline

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

Log a full day for a specific Jira isue  
```  
jlog 1d TEST-12 "write your comment message here"
```
  
Log hours and minutes  
```
jlog 2h30m TEST-12 "write your comment message here"
```   
  
Log for a specific Jira issue retrieved from a repository branch
```
# while in your repository, on a branch having a name containing a Jira issue key
jlog -b 1h45m "write your comment message here"
```
