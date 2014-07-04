-- MySQL Script generated by MySQL Workbench
-- Fri 04 Jul 2014 12:12:43 PM EDT
-- Model: New Model    Version: 1.0
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema awayteam
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `awayteam` ;
CREATE SCHEMA IF NOT EXISTS `awayteam` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `awayteam` ;

-- -----------------------------------------------------
-- Table `awayteam`.`user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`user` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`user` (
  `userId` INT NOT NULL AUTO_INCREMENT,
  `loginId` VARCHAR(45) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `userIdentifier` VARCHAR(64) NOT NULL,
  `userSecret` VARCHAR(64) NOT NULL,
  `userSalt` VARCHAR(64) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `firstName` VARCHAR(45) NOT NULL,
  `lastName` VARCHAR(45) NOT NULL,
  `cellPhone` VARCHAR(45) NOT NULL,
  `emergencyPhone` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`userId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`location` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`location` (
  `locId` INT NOT NULL AUTO_INCREMENT,
  `locName` VARCHAR(45) NOT NULL,
  `locLatitude` VARCHAR(45) NOT NULL,
  `locLongitude` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`locId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`event`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`event` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`event` (
  `eventId` INT NOT NULL AUTO_INCREMENT,
  `eventName` VARCHAR(45) NOT NULL,
  `eventDescription` VARCHAR(45) NULL,
  `eventLocationId` INT NULL,
  PRIMARY KEY (`eventId`),
  INDEX `location_idx` (`eventLocationId` ASC),
  CONSTRAINT `eventLocation`
    FOREIGN KEY (`eventLocationId`)
    REFERENCES `awayteam`.`location` (`locId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`team_schedule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`team_schedule` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`team_schedule` (
  `teamScheduleId` INT NOT NULL AUTO_INCREMENT,
  `teamScheduleEventId` INT NULL,
  `teamScheduleStartTime` DATETIME NOT NULL,
  `teamScheduleEndTime` DATETIME NOT NULL,
  PRIMARY KEY (`teamScheduleId`),
  INDEX `eventId_idx` (`teamScheduleEventId` ASC),
  CONSTRAINT `teamScheduleEventId`
    FOREIGN KEY (`teamScheduleEventId`)
    REFERENCES `awayteam`.`event` (`eventId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`team`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`team` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`team` (
  `teamId` INT NOT NULL AUTO_INCREMENT,
  `teamName` VARCHAR(45) NOT NULL,
  `teamLocationId` INT NULL,
  `teamDescription` VARCHAR(45) NULL,
  `teamManaged` TINYINT(1) NULL,
  `teamScheduleId` INT NULL,
  PRIMARY KEY (`teamId`),
  INDEX `location_idx` (`teamLocationId` ASC),
  INDEX `TeamSchedule_idx` (`teamScheduleId` ASC),
  CONSTRAINT `TeamLocation`
    FOREIGN KEY (`teamLocationId`)
    REFERENCES `awayteam`.`location` (`locId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `TeamSchedule`
    FOREIGN KEY (`teamScheduleId`)
    REFERENCES `awayteam`.`team_schedule` (`teamScheduleId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`team_tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`team_tasks` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`team_tasks` (
  `taskId` INT NOT NULL AUTO_INCREMENT,
  `taskTitle` VARCHAR(45) NOT NULL,
  `taskDescription` VARCHAR(45) NULL,
  `taskCompleted` TINYINT(1) NOT NULL DEFAULT 0,
  `taskTeamid` INT NULL,
  PRIMARY KEY (`taskId`),
  INDEX `teamId_idx` (`taskTeamid` ASC),
  CONSTRAINT `teamTasksTeamId`
    FOREIGN KEY (`taskTeamid`)
    REFERENCES `awayteam`.`team` (`teamId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`team_member`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`team_member` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`team_member` (
  `teamMemberId` INT NOT NULL AUTO_INCREMENT,
  `teamId` INT NULL,
  `userId` INT NULL,
  `manager` TINYINT(1) NULL,
  `pendingApproval` TINYINT(1) NULL,
  PRIMARY KEY (`teamMemberId`),
  INDEX `teamId_idx` (`teamId` ASC),
  INDEX `userId_idx` (`userId` ASC),
  CONSTRAINT `teamMembersTeamId`
    FOREIGN KEY (`teamId`)
    REFERENCES `awayteam`.`team` (`teamId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `teamMembersUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `awayteam`.`user` (`userId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`expense`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`expense` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`expense` (
  `expenseId` INT NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(45) NOT NULL,
  `amount` DECIMAL NOT NULL,
  `type` ENUM('breakfast','lunch','dinner','snack','other') NOT NULL,
  `date` DATE NOT NULL DEFAULT CURRENT_DATE(),
  `receipt` BLOB NULL COMMENT 'For type column integer enumeration is as follows:' /* comment truncated */ /*
1 = breakfast
2 = lunch
3 = dinner
4 = other*/,
  `teamId` INT NOT NULL,
  `userId` INT NOT NULL,
  PRIMARY KEY (`expenseId`),
  INDEX `teamId_idx` (`teamId` ASC),
  INDEX `userId_idx` (`userId` ASC),
  CONSTRAINT `ExpenseTeamId`
    FOREIGN KEY (`teamId`)
    REFERENCES `awayteam`.`team` (`teamId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ExpenseUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `awayteam`.`user` (`userId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `awayteam`.`user_location`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `awayteam`.`user_location` ;

CREATE TABLE IF NOT EXISTS `awayteam`.`user_location` (
  `userLocationId` INT NOT NULL AUTO_INCREMENT,
  `userId` INT NOT NULL,
  `locationId` INT NOT NULL,
  `userLocationTimeStamp` DATETIME NULL,
  PRIMARY KEY (`userLocationId`),
  INDEX `userLocationUserId_idx` (`userId` ASC),
  INDEX `useLocationLocationId_idx` (`locationId` ASC),
  CONSTRAINT `userLocationUserId`
    FOREIGN KEY (`userId`)
    REFERENCES `awayteam`.`user` (`userId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `useLocationLocationId`
    FOREIGN KEY (`locationId`)
    REFERENCES `awayteam`.`location` (`locId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
