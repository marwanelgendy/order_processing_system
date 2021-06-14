-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema Order_Processing_System
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema Order_Processing_System
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Order_Processing_System` DEFAULT CHARACTER SET utf8 ;
USE `Order_Processing_System` ;

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root';
flush privileges;

-- -----------------------------------------------------
-- Table `Order_Processing_System`.`publisher`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`publisher` (
  `name` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`book`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`book` (
  `ISBN` INT UNSIGNED NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `publisherName` VARCHAR(40) NOT NULL,
  `year` DATE NULL,
  `price` DOUBLE UNSIGNED NULL DEFAULT 20,
  `numberOfCopies` INT UNSIGNED NULL DEFAULT 0,
  `threshold` INT UNSIGNED NULL DEFAULT 0,
  `category` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`ISBN`),
  UNIQUE INDEX `title_UNIQUE` (`title` ASC) VISIBLE,
  INDEX `publisher_name_fk_1_idx` (`publisherName` ASC) INVISIBLE,
  INDEX `category_idx` (`category` ASC) VISIBLE,
  CONSTRAINT `publisher_name_fk_1`
    FOREIGN KEY (`publisherName`)
    REFERENCES `Order_Processing_System`.`publisher` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`bookauthor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`bookauthor` (
  `ISBN` INT UNSIGNED NOT NULL,
  `authorName` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`ISBN`, `authorName`),
  CONSTRAINT `ISBN_fk_5`
    FOREIGN KEY (`ISBN`)
    REFERENCES `Order_Processing_System`.`book` (`ISBN`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`order` (
  `orderId` INT UNSIGNED NOT NULL,
  `ISBN` INT UNSIGNED NOT NULL,
  `quantity` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`orderId`),
  INDEX `ISBN_fk_1_idx` (`ISBN` ASC) VISIBLE,
  CONSTRAINT `ISBN_fk_1`
    FOREIGN KEY (`ISBN`)
    REFERENCES `Order_Processing_System`.`book` (`ISBN`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`publisheraddress`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`publisheraddress` (
  `name` VARCHAR(40) NOT NULL,
  `address` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`name`, `address`),
  CONSTRAINT `publisher_name_fk_2`
    FOREIGN KEY (`name`)
    REFERENCES `Order_Processing_System`.`publisher` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`publisherphone`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`publisherphone` (
  `name` VARCHAR(40) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`name`, `phone`),
  CONSTRAINT `publisher_name_fk_3`
    FOREIGN KEY (`name`)
    REFERENCES `Order_Processing_System`.`publisher` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`user` (
  `name` VARCHAR(40) NOT NULL,
  `password` VARCHAR(40) NOT NULL,
  `Lname` VARCHAR(40) NOT NULL,
  `Fname` VARCHAR(40) NOT NULL,
  `Email` VARCHAR(100) NOT NULL,
  `phoneNumber` VARCHAR(15) NULL,
  `shippingAddress` VARCHAR(100) NULL,
  `isManager` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`name`),
  UNIQUE INDEX `Email_UNIQUE` (`Email` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Order_Processing_System`.`sales`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Order_Processing_System`.`sales` (
  `ISBN` INT UNSIGNED NOT NULL,
  `userName` VARCHAR(40) NOT NULL,
  `sellingDate` DATE NOT NULL,
  `sellingTime` TIME NOT NULL,
  `salesNumber` INT UNSIGNED NULL,
  `price` DOUBLE UNSIGNED NOT NULL,
  PRIMARY KEY (`ISBN`, `userName`, `sellingDate`, `sellingTime`),
  INDEX `userName_fk_1_idx` (`userName` ASC) INVISIBLE,
  CONSTRAINT `userName_fk_1`
    FOREIGN KEY (`userName`)
    REFERENCES `Order_Processing_System`.`user` (`name`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `ISBN_fk_4`
    FOREIGN KEY (`ISBN`)
    REFERENCES `Order_Processing_System`.`book` (`ISBN`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

USE `Order_Processing_System`;

DELIMITER $$
USE `Order_Processing_System`$$
CREATE DEFINER = CURRENT_USER TRIGGER `Order_Processing_System`.`book_BEFORE_INSERT` BEFORE INSERT ON `book` FOR EACH ROW
BEGIN

if new.category != 'art' 
AND new.category != 'history' 
AND new.category != 'science' 
AND new.category != 'religion' 
AND new.category != 'geography' then
signal sqlstate '45000'
set message_text = 'Category does not exist';
end if;
END$$

USE `Order_Processing_System`$$
CREATE DEFINER = CURRENT_USER TRIGGER `Order_Processing_System`.`book_BEFORE_UPDATE` BEFORE UPDATE ON `book` FOR EACH ROW
BEGIN
IF new.numberOfCopies < 0 then
signal sqlstate '45000'
set message_text = 'number of copies can not be negative';
END IF;
END$$

USE `Order_Processing_System`$$
CREATE DEFINER = CURRENT_USER TRIGGER `Order_Processing_System`.`book_AFTER_UPDATE` AFTER UPDATE ON `book` FOR EACH ROW
BEGIN
DECLARE max_id int;
if new.numberOfCopies < new.threshold and new.numberOfCopies < old.numberOfCopies 
then select max(orderid) into max_id from `order`;
if max_id is null THEN
set max_id = 0;
end if;
Insert into `order` (ISBN,orderId,quantity)
values (new.ISBN,max_id+1,new.threshold- new.numberOfCopies);
end if;



END$$

USE `Order_Processing_System`$$
CREATE DEFINER = CURRENT_USER TRIGGER `Order_Processing_System`.`order_BEFORE_DELETE` BEFORE DELETE ON `order` FOR EACH ROW
BEGIN
UPDATE book 
set numberOfCopies= numberOfCopies+ old.quantity 
where ISBN = old.ISBN;
END$$


DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
