(ns jlog.core-test
  (:require [clojure.test :refer :all]
            [jlog.core :refer :all]))

;; valid-jlog-time?
(deftest valid-jlog-time?-test
  (testing "should only return true if timelog has format 1h, 1h3m, 1h33m, 3m, or 33m. Hours has max 8, and minutes has max 60."
    (is (false? (valid-jlog-time? "1")))
    (is (false? (valid-jlog-time? "1d1h")))
    (is (false? (valid-jlog-time? "1hh")))
    (is (false? (valid-jlog-time? "10h")))
    (is (false? (valid-jlog-time? "80m")))
    (is (false? (valid-jlog-time? "1h80m")))
    (is (true? (valid-jlog-time? "1d")))
    (is (true? (valid-jlog-time? "1h")))
    (is (true? (valid-jlog-time? "1h3m")))
    (is (true? (valid-jlog-time? "1h33m")))
    (is (true? (valid-jlog-time? "3m")))
    (is (true? (valid-jlog-time? "33m")))
    (is (true? (valid-jlog-time? "8h60m")))))

(deftest space-hrs&mins-hours-and-minutes
  (testing "should seperate the hours from the minutes if both are there"
    (is (= "1h 30m" (space-hrs&mins "1h30m")))
    (is (= "1h 3m" (space-hrs&mins "1h3m")))))

(deftest space-hrs&mins-hours-or-mins-only
  (testing "should leave the time log as is if there is only hours or minutes provided"
    (is (= "30m" (space-hrs&mins "30m")))
    (is (= "3m" (space-hrs&mins "3m")))
    (is (= "1h" (space-hrs&mins "1h")))))

(deftest get-date-test
  (testing "should return a date of the format mm/dd/yyyy"
    (is (not (nil? (re-find #"^\d{2}\/\d{2}\/\d{4}" (get-date)))))))

(deftest valid-jira-issue?-invalid
  (testing "should return false if the jira is invalid"
    (is (false? (valid-jira-issue? "a-1")))
    (is (false? (valid-jira-issue? "a-10")))
    (is (false? (valid-jira-issue? "CCCCC-1000")))
    (is (false? (valid-jira-issue? "3245")))
    (is (false? (valid-jira-issue? "234")))
    (is (false? (valid-jira-issue? "a4")))))

(deftest valid-jira-issue?-valid
  (testing "should return false if the jira is invalid"
    (is (true? (valid-jira-issue? "abc-1")))
    (is (true? (valid-jira-issue? "abc-10")))
    (is (true? (valid-jira-issue? "abcd-1000")))
    (is (true? (valid-jira-issue? "AbCD-1")))))
