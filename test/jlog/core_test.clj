(ns jlog.core-test
  (:require [clojure.test :refer :all]
            [jlog.core :refer :all]))

;; get-two-args
(deftest get-two-args-empty-list
  (testing "should return a list of two empty strings if the args list is empty"
    (is (= (get-two-args []) ["" ""]))))

(deftest get-two-args-list-of-one
  (testing "should return a list of two empty strings if the args list length is one"
    (is (= (get-two-args [1]) ["" ""]))))

(deftest get-two-args-list-of-two
  (testing "should return the exact args list as the passed args"
    (is (= (get-two-args [1 2]) [1 2]))))

(deftest get-two-args-list-over-two
  (testing "should return the first two arguments of the list"
    (is (= (get-two-args [1 2 3]) [1 2]))
    (is (= (get-two-args [1 2 3 4 5 6 7 8]) [1 2]))
    (is (= (get-two-args ["try" "with" "strings"]) ["try" "with"]))))

;; has-length?
(deftest has-length?-test
  (testing "should only return true if list has indicated length"
    (is (false? (has-length? [] 1)))
    (is (true? (has-length? [1] 1)))
    (is (true? (has-length? [1 2] 2)))
    (is (false? (has-length? [1 2 3] 2)))))

(deftest has-min-length?-test
  (testing "should only return true if list has minimum indicated length"
    (is (false? (has-min-length? [] 1)))
    (is (true? (has-min-length? [1] 1)))
    (is (true? (has-min-length? [1 2] 2)))
    (is (true? (has-min-length? [1 2 3] 2)))))

;; valid-jlog-time?
(deftest valid-jlog-time?-test
  (testing "should only return true if timelog has format 1h, 1h3m, 1h33m, 3m, or 33m. Hours has max 8, and minutes has max 60."
    (is (false? (valid-jlog-time? "1")))
    (is (false? (valid-jlog-time? "1d")))
    (is (false? (valid-jlog-time? "1hh")))
    (is (false? (valid-jlog-time? "10h")))
    (is (false? (valid-jlog-time? "80m")))
    (is (false? (valid-jlog-time? "1h80m")))
    (is (true? (valid-jlog-time? "1h")))
    (is (true? (valid-jlog-time? "1h3m")))
    (is (true? (valid-jlog-time? "1h33m")))
    (is (true? (valid-jlog-time? "3m")))
    (is (true? (valid-jlog-time? "33m")))
    (is (true? (valid-jlog-time? "8h60m")))))

(deftest valid-jlog-args-list?-invalid-args-combination
  (testing "should return false if args-list is minimum of length 2 and has invalid arguments"
    (is (false? (valid-jlog-args-list? ["1h" "-t"]))) ;; invalid order
    (is (false? (valid-jlog-args-list? ["1" "2"]))) ;; invalid format both args
    (is (false? (valid-jlog-args-list? ["-t" "2"]))) ;; invalid format second arg
    (is (false? (valid-jlog-args-list? ["-l" "2h"]))) ;; invalid format first arg
    (is (false? (valid-jlog-args-list? ["-l" "-t" "2h"]))) ;; valid arguments but too far in the list 1
    (is (false? (valid-jlog-args-list? ["-t" "2" "2h3m"]))))) ;; valid arguments but too far in the list 2

(deftest valid-jlog-args-list?-valid-args
  (testing "should return true if has two valid args in args-list."
    (is (true? (valid-jlog-args-list? ["-t" "1h"])))
    (is (true? (valid-jlog-args-list? ["-t" "1h3m"])))
    (is (true? (valid-jlog-args-list? ["-t" "3m"])))
    (is (true? (valid-jlog-args-list? ["-t" "33m"])))
    (is (true? (valid-jlog-args-list? ["-t" "8h60m"])))))

(deftest space-hrs&mins-hours-and-minutes
  (testing "should seperate the hours from the minutes if both are there"
    (is (= "1h 30m" (space-hrs&mins "1h30m")))
    (is (= "1h 3m" (space-hrs&mins "1h3m")))))

(deftest space-hrs&mins-hours-or-mins-only
  (testing "should leave the time log as is if there is only hours or minutes provided"
    (is (= "30m" (space-hrs&mins "30m")))
    (is (= "3m" (space-hrs&mins "3m")))
    (is (= "1h" (space-hrs&mins "1h")))))
