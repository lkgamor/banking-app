-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema banking
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `banking` DEFAULT CHARACTER SET utf8;
USE `banking`;

-- -----------------------------------------------------
-- Table `banking`.`branch`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`branch` (
  `branch_id` VARCHAR(36) NOT NULL,
  `branch_name` VARCHAR(45) NOT NULL,
  `branch_address` VARCHAR(45) NOT NULL,
  `branch_state` VARCHAR(10),
  PRIMARY KEY (`branch_id`),
  UNIQUE INDEX `branch_id_UNIQUE` (`branch_id` ASC),
  UNIQUE INDEX `branch_name_UNIQUE` (`branch_name` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`card_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`card_type` (
  `card_type_id` VARCHAR(36) NOT NULL,
  `card_type_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`card_type_id`),
  UNIQUE INDEX `card_type_id_UNIQUE` (`card_type_id` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`role`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`role` (
  `role_id` VARCHAR(36) NOT NULL,
  `role_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `role_id_UNIQUE` (`role_id` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`employee` (
  `employee_id` VARCHAR(36) NOT NULL,
  `employee_first_name` VARCHAR(100) NOT NULL,
  `employee_last_name` VARCHAR(100),
  `employee_contact` VARCHAR(45),
  `employee_dob` DATE,
  `employee_gender` VARCHAR(45),
  `employee_address` VARCHAR(255),
  `employee_email` VARCHAR(100),
  `employee_branch` VARCHAR(100) NOT NULL,
  `employee_role` VARCHAR(36) NOT NULL,
  `date_created` DATETIME NOT NULL,
  `date_terminated` DATETIME NULL DEFAULT NULL,
  `working_status` TINYINT(5) NOT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE INDEX `employee_id_UNIQUE` (`employee_id` ASC),
  INDEX `branch_id_idx` (`employee_branch` ASC),
  INDEX `fk_Employee_Role1_idx` (`employee_role` ASC),
  CONSTRAINT `employee_branch_id`
    FOREIGN KEY (`employee_branch`)
    REFERENCES `banking`.`branch` (`branch_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Employee_Role1`
    FOREIGN KEY (`employee_role`)
    REFERENCES `banking`.`role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`account` (
  `account_number` VARCHAR(50) NOT NULL,
  `account_name` VARCHAR(100) NOT NULL,
  `account_balance` DOUBLE NOT NULL,
  `account_user_gender` VARCHAR(10) NOT NULL,
  `account_user_dob` DATE NOT NULL,
  `account_occupation` VARCHAR(50) NOT NULL,
  `account_email` VARCHAR(50) NULL DEFAULT NULL,
  `account_contact_number` VARCHAR(10) NOT NULL,
  `account_id_type` VARCHAR(36) NULL,
  `account_id_number` VARCHAR(45) NOT NULL,
  `account_kin_name` VARCHAR(100) NULL DEFAULT NULL,
  `account_kin_contact` VARCHAR(10) NULL DEFAULT NULL,
  `account_branch` VARCHAR(36) NOT NULL,
  `account_officer` VARCHAR(36) NOT NULL,
  `account_signature` MEDIUMBLOB NULL DEFAULT NULL,
  `account_image` MEDIUMBLOB NULL DEFAULT NULL,
  `account_status` TINYINT(5) NOT NULL,
  `account_date_opened` DATETIME NOT NULL,
  `account_date_closed` DATETIME NULL DEFAULT NULL,
  `loaned` TINYINT(5) NULL DEFAULT NULL,
  PRIMARY KEY (`account_number`),
  UNIQUE INDEX `account_number_UNIQUE` (`account_number` ASC),
  INDEX `fk_Account_Card_Type1_idx` (`account_id_type` ASC),
  INDEX `fk_Account_Branch1_idx` (`account_branch` ASC),
  INDEX `fk_account_employee1_idx` (`account_officer` ASC),
  CONSTRAINT `fk_Account_Branch1`
    FOREIGN KEY (`account_branch`)
    REFERENCES `banking`.`branch` (`branch_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_Account_Card_Type1`
    FOREIGN KEY (`account_id_type`)
    REFERENCES `banking`.`card_type` (`card_type_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_account_employee1`
    FOREIGN KEY (`account_officer`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`account_audit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`account_audit` (
  `account_number` VARCHAR(50) NOT NULL,
  `account_name` VARCHAR(45) NOT NULL,
  `account_contact_number` VARCHAR(10) NOT NULL,
  `account_email` VARCHAR(50) NOT NULL,
  `account_id_type` VARCHAR(50) NOT NULL,
  `account_id_number` VARCHAR(50) NOT NULL,
  `account_occupation` VARCHAR(50) NOT NULL,
  `account_balance` DOUBLE NOT NULL,
  `date_edited` DATETIME NOT NULL,
  `account_action` VARCHAR(10) NULL DEFAULT NULL,
  `created_by` VARCHAR(36) NOT NULL,
  INDEX `fk_Account_Audit_Card_Type1_idx` (`account_id_type` ASC),
  INDEX `fk_account_audit_employee1_idx` (`created_by` ASC),
  CONSTRAINT `fk_Account_Audit_Card_Type1`
    FOREIGN KEY (`account_id_type`)
    REFERENCES `banking`.`card_type` (`card_type_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_account_audit_employee1`
    FOREIGN KEY (`created_by`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`transaction`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`transaction` (
  `id` INT(5) NOT NULL AUTO_INCREMENT,
  `transaction_id` VARCHAR(45) NOT NULL,
  `transaction_type` VARCHAR(45) NOT NULL,
  `transaction_account_number` VARCHAR(50) NOT NULL,
  `transaction_account_name` VARCHAR(100) NOT NULL,
  `transaction_date` DATETIME NOT NULL,
  `transaction_amount` DOUBLE NOT NULL,
  `total_balance` DOUBLE NOT NULL,
  `transaction_issued_by` VARCHAR(36) NOT NULL,
  `transaction_branch` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `transaction_id_UNIQUE` (`transaction_id` ASC),
  INDEX `fk_Transaction_Employee1_idx` (`transaction_issued_by` ASC),
  INDEX `fk_transaction_branch1_idx` (`transaction_branch` ASC),
  CONSTRAINT `fk_Transaction_Employee1`
    FOREIGN KEY (`transaction_issued_by`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_transaction_branch1`
    FOREIGN KEY (`transaction_branch`)
    REFERENCES `banking`.`branch` (`branch_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8;



-- -----------------------------------------------------
-- Table `banking`.`deposit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`deposit` (
  `id` INT(5) NOT NULL AUTO_INCREMENT,
  `deposit_id` VARCHAR(45) NOT NULL,
  `deposit_date` DATETIME NOT NULL,
  `deposit_amount` DOUBLE NOT NULL,
  `total_balance` DOUBLE NOT NULL,
  `deposited_account_number` VARCHAR(50) NOT NULL,
  `deposited_account_name` VARCHAR(100) NOT NULL,
  `issued_by` VARCHAR(36) NOT NULL,
  `deposit_branch` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `deposit_id_UNIQUE` (`deposit_id` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_deposit_employee1_idx` (`issued_by` ASC),
  INDEX `fk_deposit_branch1_idx` (`deposit_branch` ASC),
  CONSTRAINT `fk_Deposit_Transaction1`
    FOREIGN KEY (`deposit_id`)
    REFERENCES `banking`.`transaction` (`transaction_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_deposit_employee1`
    FOREIGN KEY (`issued_by`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_deposit_branch1`
    FOREIGN KEY (`deposit_branch`)
    REFERENCES `banking`.`branch` (`branch_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`loan`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`loan` (
  `id` INT(5) NOT NULL AUTO_INCREMENT,
  `loan_id` VARCHAR(45) NOT NULL,
  `loan_date` DATETIME NOT NULL,
  `loan_amount` DOUBLE NOT NULL,
  `loan_payed` DOUBLE NULL DEFAULT NULL,
  `loan_payed_date` DATETIME NULL,
  `loan_account_number` VARCHAR(50) NOT NULL,
  `loan_account_name` VARCHAR(100) NOT NULL,
  `loan_status` TINYINT(5) NULL,
  `issued_by` VARCHAR(36) NOT NULL,
  `loan_branch` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `loan_id_UNIQUE` (`loan_id` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_loan_employee1_idx` (`issued_by` ASC),
  INDEX `fk_loan_branch1_idx` (`loan_branch` ASC),
  CONSTRAINT `loan_id`
    FOREIGN KEY (`loan_id`)
    REFERENCES `banking`.`transaction` (`transaction_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_loan_employee1`
    FOREIGN KEY (`issued_by`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_loan_branch1`
    FOREIGN KEY (`loan_branch`)
    REFERENCES `banking`.`branch` (`branch_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`loan_payment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`loan_payment` (
  `id` INT(5) NOT NULL AUTO_INCREMENT,
  `loan_id` VARCHAR(45) NOT NULL,
  `loan_payment_amount` DOUBLE NULL DEFAULT NULL,
  `loan_payment_balance` DOUBLE NULL DEFAULT NULL,
  `loan_payment_date` DATETIME NULL DEFAULT NULL,
  `loan_status` TINYINT(5) NULL DEFAULT NULL,
  `loan_payment_issued_by` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_loan_employee1_idx` (`loan_payment_issued_by` ASC),
  INDEX `loan_id0_idx` (`loan_id` ASC),
  CONSTRAINT `fk_loan_employee10`
    FOREIGN KEY (`loan_payment_issued_by`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `loan_id0`
    FOREIGN KEY (`loan_id`)
    REFERENCES `banking`.`loan` (`loan_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`transaction_audit`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`transaction_audit` (
  `audit_id` INT(5) NOT NULL AUTO_INCREMENT,
  `transaction_id` VARCHAR(50) NOT NULL,
  `transaction_type` VARCHAR(10) NOT NULL,
  `date_edited` DATETIME NOT NULL,
  `transaction_amount` DOUBLE NOT NULL,
  `total_balance` DOUBLE NOT NULL,
  `issued_by` VARCHAR(50) NOT NULL,
  `transaction_account_id` VARCHAR(45) NOT NULL,
  `transaction_account_name` VARCHAR(100) NOT NULL,
  `transaction_branch` VARCHAR(50) NULL DEFAULT NULL,
  PRIMARY KEY (`audit_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `banking`.`withdrawal`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `banking`.`withdrawal` (
  `id` INT(5) NOT NULL AUTO_INCREMENT,
  `withdrawal_id` VARCHAR(45) NOT NULL,
  `withdrawal_date` DATETIME NOT NULL,
  `withdrawal_amount` DOUBLE NOT NULL,
  `total_balance` DOUBLE NOT NULL,
  `withdrawal_account_number` VARCHAR(50) NOT NULL,
  `withdrawal_account_name` VARCHAR(100) NOT NULL,
  `issued_by` VARCHAR(36) NOT NULL,
  `withdrawal_branch` VARCHAR(36) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `withdrawal_id_UNIQUE` (`withdrawal_id` ASC),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `fk_withdrawal_employee1_idx` (`issued_by` ASC),
  INDEX `fk_withdrawal_branch1_idx` (`withdrawal_branch` ASC),
  CONSTRAINT `fk_Withdrawal_Transaction1`
    FOREIGN KEY (`withdrawal_id`)
    REFERENCES `banking`.`transaction` (`transaction_id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_withdrawal_employee1`
    FOREIGN KEY (`issued_by`)
    REFERENCES `banking`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE,
  CONSTRAINT `fk_withdrawal_branch1`
    FOREIGN KEY (`withdrawal_branch`)
    REFERENCES `banking`.`branch` (`branch_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


USE `banking`;

DELIMITER $$


USE `banking`$$
DROP TRIGGER IF EXISTS `banking`.`Account_BEFORE_UPDATE` $$
USE `banking`$$
CREATE
DEFINER=`louis`@`%`
TRIGGER `banking`.`Account_BEFORE_UPDATE`
BEFORE UPDATE ON `banking`.`account`
FOR EACH ROW
BEGIN
  INSERT INTO `account_audit` (account_number, account_name, account_contact_number, account_email, account_id_type, account_id_number, account_occupation, created_by, account_balance, date_edited, account_action) VALUES(OLD.`account_number`, OLD.`account_name`, OLD.`account_contact_number`, OLD.`account_email`, OLD.`account_id_type`, OLD.`account_id_number`, OLD.`account_occupation`, OLD.`account_officer`, OLD.`account_balance`, NOW(), "UPDATED");
END$$


USE `banking`$$
DROP TRIGGER IF EXISTS `banking`.`Account_BEFORE_DELETE` $$
USE `banking`$$
CREATE
DEFINER=`louis`@`%`
TRIGGER `banking`.`Account_BEFORE_DELETE`
BEFORE DELETE ON `banking`.`account`
FOR EACH ROW
BEGIN
  INSERT INTO `account_audit` (account_number, account_name, account_contact_number, account_email, account_id_type, account_id_number, account_occupation, created_by, account_balance, date_edited, account_action) VALUES(OLD.`account_number`, OLD.`account_name`, OLD.`account_contact_number`, OLD.`account_email`, OLD.`account_id_type`, OLD.`account_id_number`, OLD.`account_occupation`, OLD.`account_officer`, OLD.`account_balance`, NOW(), "DELETED");
END$$


-- USE `banking`$$
-- DROP TRIGGER IF EXISTS `banking`.`Transaction_AFTER_INSERT` $$
-- USE `banking`$$
-- CREATE
-- DEFINER=`louis`@`%`
-- TRIGGER `banking`.`Transaction_AFTER_INSERT`
-- AFTER INSERT ON `banking`.`transaction`
-- FOR EACH ROW
-- BEGIN
--   SET @employee = (SELECT concat(employee_first_name, ' ', employee_last_name) FROM employee WHERE employee_id = NEW.`transaction_issued_by`);
--   SET @branch = (SELECT branch_name FROM branch WHERE branch_id = NEW.`transaction_branch`);
--   IF (NEW.`transaction_type` = 'Loan') THEN
--             INSERT INTO `loan` (loan_id, loan_date, loan_amount, loan_payed, loan_account_number, loan_account_name, issued_by, loan_branch, loan_status) VALUES(NEW.`transaction_id`, NEW.`transaction_date`, NEW.`transaction_amount`, 0.00, NEW.`transaction_account_number`, NEW.`transaction_account_name`, NEW.`transaction_issued_by`, NEW.`transaction_branch`, TRUE);
--       ELSEIF (NEW.`transaction_type` = 'Withdrawal') THEN
--            INSERT INTO `withdrawal` (withdrawal_id, withdrawal_date, withdrawal_amount, total_balance, withdrawal_account_number, withdrawal_account_name, issued_by, withdrawal_branch) VALUES(NEW.`transaction_id`, NEW.`transaction_date`, NEW.`transaction_amount`, NEW.`total_balance`, NEW.`transaction_account_number`, NEW.`transaction_account_name`, NEW.`transaction_issued_by`, NEW.`transaction_branch`);
--     ELSEIF (NEW.`transaction_type` = 'Deposit') THEN
--             INSERT INTO `deposit` (deposit_id, deposit_date, deposit_amount, total_balance, deposited_account_number, deposited_account_name, issued_by, deposit_branch) VALUES(NEW.`transaction_id`, NEW.`transaction_date`, NEW.`transaction_amount`, NEW.`total_balance`, NEW.`transaction_account_number`, NEW.`transaction_account_name`, NEW.`transaction_issued_by`, NEW.`transaction_branch`);
--   END IF;
-- END$$


USE `banking`$$
DROP TRIGGER IF EXISTS `banking`.`Transaction_BEFORE_DELETE` $$
USE `banking`$$
CREATE
DEFINER=`louis`@`%`
TRIGGER `banking`.`Transaction_BEFORE_DELETE`
BEFORE DELETE ON `banking`.`transaction`
FOR EACH ROW
BEGIN
  SET @employee = (SELECT concat(employee_first_name, ' ', employee_last_name) FROM employee WHERE employee_id = OLD.`transaction_issued_by`);
  INSERT INTO `Transaction_Audit` (transaction_id, transaction_type, date_edited, transaction_amount, total_balance, transaction_account_id, transaction_account_name, issued_by, transaction_branch) VALUES(OLD.`transaction_id`, OLD.`transaction_type`, NOW() , OLD.`transaction_amount`, OLD.`total_balance`, OLD.`transaction_account_number`, @employee, OLD.`transaction_branch`);
END$$


USE `banking`$$
DROP TRIGGER IF EXISTS `banking`.`Deposit_BEFORE_DELETE` $$
USE `banking`$$
CREATE
DEFINER=`louis`@`%`
TRIGGER `banking`.`Deposit_BEFORE_DELETE`
BEFORE DELETE ON `banking`.`deposit`
FOR EACH ROW
BEGIN
  SET @employee = (SELECT concat(employee_first_name, ' ', employee_last_name) FROM employee WHERE employee_id = OLD.`issued_by`);
  INSERT INTO `Transaction_Audit` (transaction_id, transaction_type, date_edited, transaction_amount, total_balance, transaction_account, issued_by, transaction_branch) VALUES(OLD.`deposit_id`, "Deposit", NOW() , OLD.`deposit_amount`, OLD.`total_balance`, OLD.`deposited_account_number`, @employee, OLD.`deposit_branch`);
END$$


USE `banking`$$
DROP TRIGGER IF EXISTS `banking`.`Loan_BEFORE_DELETE` $$
USE `banking`$$
CREATE
DEFINER=`louis`@`%`
TRIGGER `banking`.`Loan_BEFORE_DELETE`
BEFORE DELETE ON `banking`.`loan`
FOR EACH ROW
BEGIN
  SET @employee = (SELECT concat(employee_first_name, ' ', employee_last_name) FROM employee WHERE employee_id = OLD.`issued_by`);
  INSERT INTO `Transaction_Audit` (transaction_id, transaction_type, date_edited, transaction_amount, total_balance, transaction_account, issued_by, transaction_branch) VALUES(OLD.`loan_id`, "Loan", NOW() , OLD.`loan_amount`, '0.0', OLD.`loan_account_number`, @employee, OLD.`loan_branch`);
END$$


USE `banking`$$
DROP TRIGGER IF EXISTS `banking`.`Withdrawal_BEFORE_DELETE` $$
USE `banking`$$
CREATE
DEFINER=`louis`@`%`
TRIGGER `banking`.`Withdrawal_BEFORE_DELETE`
BEFORE DELETE ON `banking`.`withdrawal`
FOR EACH ROW
BEGIN
  SET @employee = (SELECT concat(employee_first_name, ' ', employee_last_name) FROM employee WHERE employee_id = OLD.`issued_by`);
  INSERT INTO `Transaction_Audit` (transaction_id, transaction_type, date_edited, transaction_amount, total_balance, transaction_account, issued_by, transaction_branch) VALUES(OLD.`withdrawal_id`, "Withdrawal", NOW() , OLD.`withdrawal_amount`, OLD.`total_balance`, OLD.`withdrawal_account_number`, @employee, OLD.`withdrawal_branch`);
END$$


DELIMITER ;

/*
Create Main Branch for company in this database.
This branch is meant to be used to setup
some basic employee configurations for this database.
*/

-- MySQL procedure
DELIMITER $$
CREATE PROCEDURE AddMainBranchInfo()
BEGIN
  # MySQL-style single line comment
  INSERT INTO branch (branch_id, branch_name, branch_address, branch_state) VALUES ('fb515062-4f6b-4977-a41c-80934d79c283', 'MAIN BRANCH', 'LAPAZ', 'active');
END$$
DELIMITER;

CALL AddMainBranchInfo();

/*
Create Default Role for company in this database.
*/

-- MySQL procedure
DELIMITER $$
CREATE PROCEDURE AddRoleInfo()
BEGIN
  # MySQL-style single line comment
  INSERT INTO role (role_id, role_name) VALUES ('e0ae5d5a-2a72-4d73-8fab-2d0e116e41ff', 'MANAGER');
END$$
DELIMITER;

CALL AddRoleInfo();


/*
Create Supported ID Cards for company in this database.
*/

-- MySQL procedure
DELIMITER $$
CREATE PROCEDURE AddSupportedCardsInfo()
BEGIN
  # MySQL-style single line comment
  INSERT INTO card_type (card_type_id, card_type_name) VALUES ('7149128d-0c18-490e-983a-3c5b5195ce8d', 'VOTERS ID'), 
                                                              ('c7d50091-b433-47b1-9afe-75b652cddfda', 'DRIVERS LICENSE'),
                                                              ('31217acb-7ae0-4f5d-9cef-deefadd62afb', 'NATIONAL ID CARD'),
                                                              ('b852fd82-8b60-4e77-9bfb-8d80df77ceda', 'PHONE NUMBER');
END$$
DELIMITER;

CALL AddSupportedCardsInfo();


/*
Create Anonymous user for company in this database.
*/

-- MySQL procedure
DELIMITER $$
CREATE PROCEDURE AddAnonymousUserInfo()
BEGIN
  # MySQL-style single line comment
  INSERT INTO employee (employee_id, employee_first_name, employee_last_name, employee_contact, employee_dob, employee_gender, employee_address, employee_email, employee_branch, employee_role, date_created, working_status) 
  VALUES ('a471e78d-d244-48fd-a08f-9d91e7123040', 'ANONYMOUS', 'USER', '+233542411666', '1970-01-01', 'UNSPECIFIED', 'ASGUARD', 'lkgamor@gmail.com', 'fb515062-4f6b-4977-a41c-80934d79c283', 'e0ae5d5a-2a72-4d73-8fab-2d0e116e41ff', '1970-01-01 00:00:00', true);
END$$
DELIMITER;

CALL AddAnonymousUserInfo();