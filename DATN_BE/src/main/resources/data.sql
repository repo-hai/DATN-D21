-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: new_bej_sp3
-- ------------------------------------------------------
-- Server version	8.4.7

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `id` varchar(255) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `display_order` int NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `link_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
INSERT INTO `brand` VALUES (1,'Apple'),(2,'Samsung'),(3,'Xiaomi'),(4,'ASUS'),(5,'Lenovo'),(6,'Dell'),(7,'HP'),(8,'Sony');
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `id` varchar(255) NOT NULL,
  `added_at` date DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `attribute_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7ysjorv4gw0drqhrgehr9nd3a` (`attribute_id`),
  KEY `FKjnaj4sjyqjkr4ivemf9gb25w` (`user_id`),
  CONSTRAINT `FK7ysjorv4gw0drqhrgehr9nd3a` FOREIGN KEY (`attribute_id`) REFERENCES `product_attribute` (`id`),
  CONSTRAINT `FKjnaj4sjyqjkr4ivemf9gb25w` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES ('029d3228-5b06-46ba-91b0-4b53215cef2a','2026-01-05','Màu đen',1600000,'Vỏ iPhone 12 Pro Max',1,'27c5e7f7-5bae-4ef4-ae93-90ea01eee7f1','33ff2646-2922-416c-8216-c877201ed659'),('09e8ae2a-c4d5-44f6-bd28-095e6d76975b','2026-01-05','Trắng',99000,'Cáp Pisen Quick - Mr White Type-C to Type-C PD 60w 1m',1,'22cc47ba-2d81-4fee-b411-78fc6de99fb1','33ff2646-2922-416c-8216-c877201ed659'),('38006691-d488-417c-aa45-8403c4ee31ef','2025-12-07','Tím Oải Hương',24990000,'iPhone 17 256GB - Chính hãng Apple Việt Nam',1,'c0c5e0c9-6114-4988-bd67-d6520e7e4837','24daab77-a1cb-4e14-b002-cb0754acbc52');
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (10,'Điện Thoại','DT'),(11,'Dịch Vụ','DVV'),(12,'Phụ Kiện','PK'),(24,'Linh Kiện','LK'),(33,'Sửa chữa','SC');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fcm_device_token`
--

DROP TABLE IF EXISTS `fcm_device_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fcm_device_token` (
  `id` varchar(255) NOT NULL,
  `last_used` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlnb5a9bngbhyqiow17xgpn8m5` (`token`),
  KEY `FKb819s29v9iebdx6egnu2dwboa` (`user_id`),
  CONSTRAINT `FKb819s29v9iebdx6egnu2dwboa` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fcm_device_token`
--

LOCK TABLES `fcm_device_token` WRITE;
/*!40000 ALTER TABLE `fcm_device_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `fcm_device_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invalidated_token`
--

DROP TABLE IF EXISTS `invalidated_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invalidated_token` (
  `id` varchar(255) NOT NULL,
  `expiry_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invalidated_token`
--

LOCK TABLES `invalidated_token` WRITE;
/*!40000 ALTER TABLE `invalidated_token` DISABLE KEYS */;
INSERT INTO `invalidated_token` VALUES ('0456355e-339e-4b73-bfac-c09b4c4ef917','2026-02-03 17:07:11.000000'),('04c69e7b-6fa0-4524-bd0b-810459635b3c','2025-04-02 15:27:51.000000'),('05c6a35d-5ff9-4636-ac0a-b5c27e22613e','2025-04-02 16:35:57.000000'),('0d06c23f-8b67-4f85-a0ae-93505531e26b','2026-01-06 04:25:03.000000'),('104a26f8-1a58-4b5c-bfb4-d4bdefd776de','2025-11-07 23:59:21.000000'),('106e3351-b504-4f5f-bf59-dc2bad9d9817','2026-02-03 17:08:52.000000'),('10c242cf-f2b9-4697-9f7e-2eefac7c919f','2026-01-08 09:13:12.000000'),('122d7fb5-2be5-48a5-8a2e-d84a2c6d09b6','2026-01-18 18:07:08.000000'),('198d41eb-bbac-4ab5-9a1c-19d7eb496360','2025-04-02 16:05:37.000000'),('2947d4bb-5bd5-4642-b4b4-2e362bdd8b21','2026-01-18 18:14:21.000000'),('33041d8e-5815-4dc5-a44d-2487e3002b06','2025-04-02 16:41:11.000000'),('39eb7e24-c938-4d80-9950-32355979884c','2025-04-02 16:07:54.000000'),('43fd8fa1-ed4a-4816-beab-21e539ad624c','2026-01-18 17:44:42.000000'),('466bd302-75fa-45f4-b9ee-4eb0b060aca2','2025-04-02 16:53:52.000000'),('4673a5d1-d548-4dc6-8472-ed919234b24c','2026-01-08 09:16:52.000000'),('4f7607e1-de89-49ac-b273-0815b7b2c9c6','2025-04-02 15:14:22.000000'),('66f2e950-566f-4c38-ac09-b0eac84e7d32','2025-04-02 16:17:11.000000'),('6ef1abdc-964a-41cf-9afe-a59ce7058232','2026-01-18 15:41:44.000000'),('767467bb-a37d-4eab-b710-eede84d09424','2025-04-02 16:43:32.000000'),('8882a836-a1bb-4c48-b392-74d005c2a910','2025-04-02 16:40:25.000000'),('89606b19-82b6-4f33-b998-5eaeffb0867c','2025-04-02 16:24:37.000000'),('8a411fab-dae5-4752-97c5-2f5e12d50eb8','2025-04-02 16:36:41.000000'),('8edb7c59-047a-4f4d-bf11-18758bce4528','2025-11-07 23:57:58.000000'),('9d24daad-527a-4974-8953-1d64c40f87e0','2026-01-09 14:27:33.000000'),('9e49a3f0-da89-4dc5-a3a6-339717266977','2026-01-09 15:06:16.000000'),('a890a650-0b87-4493-9b35-9181bedfeb6b','2026-01-18 18:14:02.000000'),('ae753261-fa61-4a4b-9200-db29dbeecb04','2026-01-08 10:23:59.000000'),('b56ef505-dec4-4f9d-b15f-c0731b6f4dca','2026-02-02 18:34:09.000000'),('bfb2211c-5fc9-4777-a25d-7223c13d5909','2025-04-02 16:34:42.000000'),('c2db5871-6442-4832-b269-7b00af3ae9d8','2025-04-02 16:45:58.000000'),('c3daa5d2-6397-4690-bf78-dc84126b71f7','2025-04-02 16:46:52.000000'),('cca41f47-8cb2-4dfd-a40c-d57fad3d04ca','2025-04-02 16:51:38.000000'),('d1b08af2-e4c9-48c8-a261-9077370b786a','2025-04-02 15:41:40.000000'),('d6f39468-935f-482e-982f-5493a5570f56','2026-01-18 18:06:44.000000'),('db61c0b8-e1a0-47ad-a4f1-a9de3996fc40','2025-04-02 16:37:15.000000'),('e365086d-9ea9-4b79-86b5-806d15e551e9','2026-01-09 15:09:48.000000'),('eb2c93a9-d4d6-4d48-b761-ff0e2dfa78e4','2025-11-08 00:48:48.000000'),('ef717bfd-e608-44ab-9e2a-00477114b23b','2025-04-02 16:48:41.000000'),('f41f72d9-288b-400b-99a8-110d814eaf70','2025-11-10 04:33:12.000000'),('fa901276-deec-4765-9416-43722b64ab21','2026-01-09 15:18:04.000000');
/*!40000 ALTER TABLE `invalidated_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` varchar(255) NOT NULL,
  `body` varchar(1000) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `resource_id` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `type` enum('GENERAL_ANNOUNCEMENT','NEW_PROMOTION','ORDER_PLACED','ORDER_STATUS_UPDATE','REPAIR_REQUEST_RECEIVED','REPAIR_STATUS_UPDATE','REPAIR_TECHNICIAN_MESSAGE') NOT NULL,
  `recipient_user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgtksickis1kjl98281hxsqsc0` (`recipient_user_id`),
  CONSTRAINT `FKgtksickis1kjl98281hxsqsc0` FOREIGN KEY (`recipient_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES ('029751a6-9441-46d1-bf36-0603b91fffb3','Đơn hàng #f0faf57e-d3b0-4d47-a741-f872826b03a5 đã được cập nhật: Đã thanh toán','2025-12-07 09:13:04.792181',_binary '\0','f0faf57e-d3b0-4d47-a741-f872826b03a5','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('02cd8362-47ba-4e40-a4a2-3ff52584ef4c','Đơn hàng #32082142-3208-4c0f-bb69-2f9ca462ae56 đã được cập nhật: Đã xác nhận - aasdsasdas','2025-12-13 12:10:49.732741',_binary '\0','32082142-3208-4c0f-bb69-2f9ca462ae56','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('04899c67-22fc-4232-99ba-5b86114cc6ec','Đơn hàng #32082142-3208-4c0f-bb69-2f9ca462ae56 đã được cập nhật: Đã xác nhận - aaaaaa','2025-12-13 11:44:17.566046',_binary '\0','32082142-3208-4c0f-bb69-2f9ca462ae56','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('233fb049-7087-4c44-b60e-d077258c43a1','Đơn hàng #30adcfca-21c9-4248-8897-274d38b500f8 đã được cập nhật: Đã xác nhận - đang sửa chữa','2025-12-07 14:08:12.394046',_binary '\0','2','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('4af388fb-0356-42e5-bd9e-72734a9747de','Đơn hàng #994e8b08-ae0d-4989-b5eb-b21e6bd20b7c đã được cập nhật: Đã xác nhận - chờ xác nhận','2025-12-14 13:00:04.716995',_binary '\0','994e8b08-ae0d-4989-b5eb-b21e6bd20b7c','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('4d42728b-55db-4268-bf9e-a03329e7d419','Đơn hàng #2078da1d-553c-4e88-95e5-8ec565e6e06d đã được cập nhật: Thanh toán thất bại - test','2026-01-05 12:09:31.031823',_binary '\0','3','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('59037cd5-50aa-4138-946f-3b757a207a4e','Đơn hàng #f0faf57e-d3b0-4d47-a741-f872826b03a5 đã được cập nhật: Đã thanh toán','2025-12-07 09:13:20.335485',_binary '\0','f0faf57e-d3b0-4d47-a741-f872826b03a5','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('5d0d9b3d-0b7b-4155-9900-bd7b3cec65e2','Đơn hàng #32082142-3208-4c0f-bb69-2f9ca462ae56 đã được cập nhật: Đã xác nhận - asdadasdasd','2025-12-13 11:45:49.778599',_binary '\0','32082142-3208-4c0f-bb69-2f9ca462ae56','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('617c823f-9a72-4002-8f88-34c24cf2ab5e','Đơn hàng #2078da1d-553c-4e88-95e5-8ec565e6e06d đã được cập nhật: Thanh toán thất bại - đang xử lí','2025-12-27 10:26:55.849070',_binary '\0','2078da1d-553c-4e88-95e5-8ec565e6e06d','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('70e89552-c088-48d7-a82e-f3246e7707ba','Đơn hàng #c16d490b-b60c-4b4a-88eb-dffa03edc386 đã được cập nhật: Đã xác nhận - cập nhật linh kiện sử dụng','2025-12-14 13:20:42.364593',_binary '\0','c16d490b-b60c-4b4a-88eb-dffa03edc386','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('7e24a264-142b-4651-b4b3-016ae5200e6a','Đơn hàng #30adcfca-21c9-4248-8897-274d38b500f8 đã được cập nhật: Chờ xử lý - adu','2025-12-07 12:39:23.686657',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('91cd7b5e-57f8-4364-ae99-936df6c099f3','Đơn hàng #30adcfca-21c9-4248-8897-274d38b500f8 đã được cập nhật: Đã thanh toán','2025-12-07 14:07:04.448393',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('9abe0333-23d8-4cd2-b668-0d7e91f83005','Đơn hàng #30adcfca-21c9-4248-8897-274d38b500f8 đã được cập nhật: Chờ xử lý - 23425ewgfedfgdfg','2025-12-07 12:47:51.508861',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('9d6731c2-7369-4a0c-8777-e307749536cd','Đơn hàng #46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5 đã được cập nhật: Chờ xử lý - ssss','2025-12-07 09:20:12.558990',_binary '\0','46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('9e323b64-7fe3-4c7a-9f98-42f575edd958','Đơn hàng #30adcfca-21c9-4248-8897-274d38b500f8 đã được cập nhật: Chờ xử lý - asdasdasdasd','2025-12-07 12:47:46.821983',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('9ee1493f-aa0f-49d5-9ba6-f62dfb19ca41','Đơn hàng #2078da1d-553c-4e88-95e5-8ec565e6e06d đã được cập nhật: Đã xác nhận - 123','2025-12-27 09:54:56.751112',_binary '\0','2078da1d-553c-4e88-95e5-8ec565e6e06d','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('a22b792b-2c28-494e-a9f2-6f0a9650a8f9','Đơn hàng #b63534 của bạn đã được giao thành công.','2025-11-12 15:30:00.000000',_binary '','b63534c6-c51b-4164-9e2c-591efb2f7ef6','Đơn hàng đã giao','ORDER_STATUS_UPDATE','6895cccc-4891-4c15-981a-e59a2d16a939'),('a5da1f15-bbbf-4f56-9960-95497cba416e','Đơn hàng #30adcfca-21c9-4248-8897-274d38b500f8 đã được cập nhật: Chờ xử lý - dự tính chi phí\nchờ khách gửi hàng','2025-12-07 14:03:48.658519',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('ae115945-914a-418e-8086-098711f84829','Đơn hàng #37036055-b58b-4cbb-9437-6aba043cd4ad đã được cập nhật: Đã xác nhận - chờ xác nhận đơn','2025-12-14 12:30:53.294386',_binary '\0','37036055-b58b-4cbb-9437-6aba043cd4ad','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('b150ad1d-c9c5-4ae9-a60a-306ad3f5b3d4','Đơn hàng #f0faf57e-d3b0-4d47-a741-f872826b03a5 đã được cập nhật: Đã xác nhận','2025-12-07 09:12:56.123882',_binary '\0','f0faf57e-d3b0-4d47-a741-f872826b03a5','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('b33c803c-3d39-4a5f-b0a3-7a1b0761b9a0','Giảm giá 20% cho tất cả các dịch vụ sửa chữa. Đừng bỏ lỡ!','2025-11-13 11:00:00.000000',_binary '\0',NULL,'Khuyến mãi cuối tuần!','NEW_PROMOTION','7cacd02a-7e5b-4321-ba46-b3500ccf8589'),('bb92bd0f-bd28-463a-a906-cc4a1f3d1429','Đơn hàng #4eec3ad4-1290-431d-b619-e04a3174812c đã được cập nhật: Đang giao hàng - hủy đơn','2026-01-04 11:59:38.420647',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','6895cccc-4891-4c15-981a-e59a2d16a939'),('c028c34a-ddf3-41c8-ac5f-f7a466346581','Đơn hàng #f0faf57e-d3b0-4d47-a741-f872826b03a5 đã được cập nhật: Chờ xử lý','2025-12-07 09:09:42.550743',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('c44d914d-4e4a-4b6a-c1b4-8b2c1872c0b1','Khách hàng (ID: ...a939) vừa tạo một yêu cầu sửa chữa mới cho iPhone 17.','2025-11-13 12:00:00.000000',_binary '\0','R-12345','Yêu cầu sửa chữa mới','REPAIR_REQUEST_RECEIVED','b78ae41e-2ec3-4065-a876-06a16a039f15'),('d4cdceae-7de4-4ac7-9313-f0296e0146c0','Đơn hàng #37036055-b58b-4cbb-9437-6aba043cd4ad đã được cập nhật: Đã xác nhận - a','2025-12-14 12:56:32.665550',_binary '\0','37036055-b58b-4cbb-9437-6aba043cd4ad','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('d7b793d1-ce7f-4880-9029-cd12c507b09e','Đơn hàng #2fce0551-f869-4464-a681-732411d9c5ba đã được cập nhật: Chờ xử lý - qqeqweqweqw','2025-12-09 04:33:23.032654',_binary '\0','0','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('ddfd8ce3-8317-4af0-bd7f-95c6cc7e2fc4','Đơn hàng #2078da1d-553c-4e88-95e5-8ec565e6e06d đã được cập nhật: Đang giao hàng - lí do hủy','2026-01-05 12:21:03.611195',_binary '\0','4','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('e11a681a-1b17-483d-98e1-5e9f8549f7e8','Đơn hàng #c1828a của bạn đã được xác nhận và đang được chuẩn bị.','2025-11-13 10:00:00.000000',_binary '\0','c1828a7b-92fb-4e18-84bc-3ef957cf937f','Đơn hàng đã được xác nhận','ORDER_STATUS_UPDATE','6895cccc-4891-4c15-981a-e59a2d16a939'),('e1c8adba-873e-43b9-972d-0deb41ace7e0','Đơn hàng #16bc5bc9-93fd-4619-aad9-ab1b3020b92b đã được cập nhật: Đã thanh toán - xác nhận','2025-12-27 09:50:39.650928',_binary '\0','16bc5bc9-93fd-4619-aad9-ab1b3020b92b','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('efc73362-d17c-4111-b6f5-94d35e64590b','Đơn hàng #40c9f706-279f-4d88-b5c1-1813f4b75a7b đã được cập nhật: Đã xác nhận - cập nhật\n','2025-12-21 08:17:39.903875',_binary '','40c9f706-279f-4d88-b5c1-1813f4b75a7b','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('f1ffc8bb-1c23-4b0b-80d9-675a07bef0d8','Đơn hàng #32082142-3208-4c0f-bb69-2f9ca462ae56 đã được cập nhật: Đã xác nhận - asdasdasd','2025-12-13 11:49:51.009394',_binary '\0','1','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','33ff2646-2922-416c-8216-c877201ed659'),('f24c9db5-39ea-440a-9ea7-02e389da5bd7','Đơn hàng #46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5 đã được cập nhật: Chờ xử lý - test 1','2025-12-07 09:19:54.154754',_binary '\0','46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('f4d7a8e1-fe47-4344-b50a-99a99a037585','Đơn hàng #46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5 đã được cập nhật: Chờ xử lý','2025-12-07 09:25:06.837814',_binary '\0','46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','24daab77-a1cb-4e14-b002-cb0754acbc52'),('fe5e9afa-c6ef-451e-989d-4c3d2e628346','Đơn hàng #2078da1d-553c-4e88-95e5-8ec565e6e06d đã được cập nhật: Đã thanh toán - 2555','2025-12-27 09:54:41.772916',_binary '\0','2078da1d-553c-4e88-95e5-8ec565e6e06d','Cập nhật đơn hàng','ORDER_STATUS_UPDATE','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('n1a2b3c4-d5e6-7890-abcd-ef1234567890','Đơn hàng của bạn đã được tạo thành công. Mã đơn hàng: #o1a2b3c4','2025-11-15 10:15:00.000000',_binary '\0','o1a2b3c4-d5e6-7890-abcd-ef1234567890','Đơn hàng mới đã được tạo','ORDER_PLACED','a1b2c3d4-e5f6-7890-abcd-ef1234567890'),('n2b3c4d5-e6f7-8901-bcde-f12345678901','Đơn hàng #o2b3c4d5 của bạn đang được vận chuyển.','2025-11-16 14:20:00.000000',_binary '\0','o2b3c4d5-e6f7-8901-bcde-f12345678901','Đơn hàng đang được giao','ORDER_STATUS_UPDATE','b2c3d4e5-f6a7-8901-bcde-f12345678901'),('n3c4d5e6-f7a8-9012-cdef-123456789012','Hệ thống sẽ bảo trì vào ngày 25/11/2025 từ 2h-4h sáng.','2025-11-17 09:00:00.000000',_binary '\0',NULL,'Thông báo hệ thống','GENERAL_ANNOUNCEMENT','c3d4e5f6-a7b8-9012-cdef-123456789012'),('n4d5e6f7-a8b9-0123-def0-234567890123','Giảm giá lên đến 50% cho tất cả sản phẩm điện tử. Áp dụng từ 24-26/11/2025.','2025-11-18 08:30:00.000000',_binary '\0',NULL,'Khuyến mãi Black Friday','NEW_PROMOTION','d4e5f6a7-b8c9-0123-def0-234567890123'),('n5e6f7a8-b9c0-1234-ef01-345678901234','Đơn hàng #o5e6f7a8 của bạn đã được xác nhận và đang được chuẩn bị.','2025-11-19 11:45:00.000000',_binary '','o5e6f7a8-b9c0-1234-ef01-345678901234','Đơn hàng đã được xác nhận','ORDER_STATUS_UPDATE','e5f6a7b8-c9d0-1234-ef01-345678901234'),('n6f7a8b9-c0d1-2345-f012-456789012345','Yêu cầu sửa chữa của bạn đang được xử lý. Dự kiến hoàn thành trong 3-5 ngày.','2025-11-20 13:00:00.000000',_binary '\0','R-67890','Trạng thái sửa chữa đã cập nhật','REPAIR_STATUS_UPDATE','a1b2c3d4-e5f6-7890-abcd-ef1234567890');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `quantity` int NOT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `attribute_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  KEY `FKsengwq8ybpln1qnhy438hfr93` (`attribute_id`),
  CONSTRAINT `FKsengwq8ybpln1qnhy438hfr93` FOREIGN KEY (`attribute_id`) REFERENCES `product_attribute` (`id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES ('03658116-90fd-4a14-bf1c-422d608a7535',1150000,1,'c16d490b-b60c-4b4a-88eb-dffa03edc386','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('05e442e0-4894-4d5b-931a-d6b031cd0c18',41990000,1,'2fce0551-f869-4464-a681-732411d9c5ba','cd3841c8-3587-4d0c-a237-779287830cfa'),('0a96b69e-6999-4826-b3cc-aefade31c507',1150000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('11e090e1-79c6-4cd1-b55b-031495018a9f',1100000,1,'40f4cac8-d02e-46d8-84bc-c71d13849c48','0256c9b9-93c0-4be6-97fc-6bf0cc568142'),('14d797ae-cb78-4af9-af6d-7cdf84be1eae',37990000,1,'9e29675c-e0e3-4bc0-9125-00b959b8097b','74060578-7be8-48ad-bae4-ad8f9f81b624'),('15d248cb-6b20-443e-a048-22038526456c',30990000,1,'46e7f0c8-7e87-45f8-9d61-16bb1e8d1ff5','d6160e8c-c48a-4548-9acc-a4c93d6eb007'),('1614a48f-4b9b-4027-809d-2b4029244642',1150000,1,'37036055-b58b-4cbb-9437-6aba043cd4ad','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('292c43fe-49ef-4428-9861-3730b213de8f',1150000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('3b8eb51f-000b-4ac5-9ae0-f424f1c7d320',200000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3'),('3cce0b2c-1fc1-4d18-b8a6-ff1917c40f74',1000000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','6baa992a-2960-4668-80b2-8e7299738dbc'),('3d16e477-7e8f-4b2c-a740-6b7c724a85ec',1000000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','6baa992a-2960-4668-80b2-8e7299738dbc'),('3f48bc61-d41c-410b-b5ed-e385fe683bc6',1150000,1,'40c9f706-279f-4d88-b5c1-1813f4b75a7b','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('3f9498e2-45a5-4cd8-a28c-a611d6e2cc0d',1000000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','6baa992a-2960-4668-80b2-8e7299738dbc'),('45562de1-1173-4575-bb20-282877ae97bb',41990000,1,'32082142-3208-4c0f-bb69-2f9ca462ae56','b7b7dda1-d5c5-40d9-839e-37dd2cadec9d'),('4657a925-6d29-45d4-ac1c-4accc2ff2b11',1150000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('52372bd7-8aa7-4f00-8d83-98393d6c72c2',1150000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('56e945ef-db24-4aaa-b3b0-250e3b58fa2f',1150000,1,'37036055-b58b-4cbb-9437-6aba043cd4ad','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('71a428d4-abd0-4a14-820d-268080b5c8c5',41990000,1,'9e29675c-e0e3-4bc0-9125-00b959b8097b','b7b7dda1-d5c5-40d9-839e-37dd2cadec9d'),('7a204c09-d4a1-4465-9e7f-09b2b15db07b',1200000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','c4af9cee-db4d-4d90-a916-d82da1ea7295'),('8672ce96-205f-4c51-8724-78d1df3da5ae',30990000,1,'39d3400d-a14f-413d-8151-5c2dade6065e','2f6d8da8-0b89-41a1-8401-f14ef3bb3962'),('8dc831b1-4265-4156-923e-cdcafa1723e2',24990000,1,'debdd91c-c74c-4014-b963-2d507684efc8','9eb6b200-3f86-4ff5-a220-94b73104ee52'),('93348210-7226-45d9-b383-08b02f249f5d',200000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3'),('9505db94-6ff6-4835-ae1a-ad861804044f',1150000,1,'37036055-b58b-4cbb-9437-6aba043cd4ad','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('a1b9ed3d-42b4-4be6-9012-0b41616ec95e',30990000,1,'32082142-3208-4c0f-bb69-2f9ca462ae56','37c1a9e8-2664-4f54-a6e6-2d555d05712d'),('a1bd7ebb-ff22-4b34-adaa-2ec52cc90427',1150000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('a43423c0-be10-4ba8-bd62-ee86d1456e87',30990000,1,'82774596-0c83-47b2-953e-fb78a81e17ba','2f6d8da8-0b89-41a1-8401-f14ef3bb3962'),('a49bd119-deee-46ad-881a-df14198e9d99',1150000,1,'37036055-b58b-4cbb-9437-6aba043cd4ad','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('b22ca408-627a-41eb-a00c-41ed05a9ae4a',1150000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('b292c2b7-5161-42c9-80ce-44300e5bc7ad',30990000,1,'f0faf57e-d3b0-4d47-a741-f872826b03a5','2f6d8da8-0b89-41a1-8401-f14ef3bb3962'),('b4224fd9-0f6f-4c6f-b335-1c3663ead525',200000,1,'40c9f706-279f-4d88-b5c1-1813f4b75a7b','bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3'),('ca2dea05-9d8f-40d6-8cd6-327451c240f7',200000,1,'7f65266a-ba7f-4126-92f7-c81ebe72b981','bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3'),('cfe490a4-77dc-4b67-839e-309c7c8c2ddf',30990000,2,'d5326a74-fae2-480f-9eba-6a5094484aaf','2f6d8da8-0b89-41a1-8401-f14ef3bb3962'),('da9420cb-e672-488e-ba89-bf5ae70c9830',23690000,1,'16bc5bc9-93fd-4619-aad9-ab1b3020b92b','daa4ebe7-573a-4427-9681-9b0c45e7b4c0'),('ee9d399d-a723-43d2-bd33-32f732dac8cf',1150000,1,'7f65266a-ba7f-4126-92f7-c81ebe72b981','ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053'),('efb6914c-d8d5-4017-ba79-98a067814146',200000,1,'2078da1d-553c-4e88-95e5-8ec565e6e06d','bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3'),('f197196c-2ae1-4cba-879f-4f19faf95893',21990000,1,'4eec3ad4-1290-431d-b619-e04a3174812c','d2cf5938-f8a8-45e3-8641-e35e206903f3'),('fb74d5de-3b73-4463-b2ae-a9b789110a87',200000,1,'37036055-b58b-4cbb-9437-6aba043cd4ad','bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3'),('fead4274-3ba5-4c46-bfc8-174ca8cb1be1',1100000,1,'40c9f706-279f-4d88-b5c1-1813f4b75a7b','0256c9b9-93c0-4be6-97fc-6bf0cc568142'),('ff60f77e-10ca-4b85-982b-83f1beaadae0',1200000,1,'994e8b08-ae0d-4989-b5eb-b21e6bd20b7c','c4af9cee-db4d-4d90-a916-d82da1ea7295');
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_note`
--

DROP TABLE IF EXISTS `order_note`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_note` (
  `id` varchar(255) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7oseoi525qtfc1ku5247l77gi` (`order_id`),
  KEY `FK6ivnl33gom046f86n2voe59j9` (`user_id`),
  CONSTRAINT `FK6ivnl33gom046f86n2voe59j9` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK7oseoi525qtfc1ku5247l77gi` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_note`
--

LOCK TABLES `order_note` WRITE;
/*!40000 ALTER TABLE `order_note` DISABLE KEYS */;
INSERT INTO `order_note` VALUES ('11340412-a7a2-44bb-97ae-4a48da291f1e','cập nhật\n','2025-12-21 08:17:39.876601','40c9f706-279f-4d88-b5c1-1813f4b75a7b','33ff2646-2922-416c-8216-c877201ed659'),('17378cb7-d9b6-46c2-8bfe-7d0d85491759','123','2025-12-27 09:54:56.743561','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('1841f380-1931-474f-bae5-caaa080c202f','Cập nhật linh kiện sử dụng','2025-12-27 10:38:07.473064','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('34ca5cf5-3ea3-4838-abe9-be6ffd152de1','Cập nhật linh kiện sử dụng','2025-12-27 10:39:03.406333','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('424f11fb-5313-4ce7-a0e4-0f24288e1f5e','Cập nhật linh kiện sử dụng','2025-12-27 11:13:14.549657','40c9f706-279f-4d88-b5c1-1813f4b75a7b','33ff2646-2922-416c-8216-c877201ed659'),('445e76fd-1584-4ffb-9826-9d4e30ad18cf','Cập nhật linh kiện sử dụng','2025-12-27 10:43:54.427255','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('452ef666-7690-4c8b-ac8e-b21e67c2de46','Cập nhật linh kiện sử dụng','2025-12-21 11:26:50.104322','40c9f706-279f-4d88-b5c1-1813f4b75a7b','33ff2646-2922-416c-8216-c877201ed659'),('4b79a1ae-67de-4eb0-bf4c-e1528e1cc8a2','hủy đơn','2026-01-04 11:59:38.377341','4eec3ad4-1290-431d-b619-e04a3174812c','33ff2646-2922-416c-8216-c877201ed659'),('4eebe910-305e-4e67-bc92-bd96430a4808','Cập nhật linh kiện sử dụng','2026-01-05 12:16:34.127705','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('70cec135-27ce-41bc-ad46-522b4b87d514','test','2026-01-05 12:09:30.961710','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('7f989b89-f491-4a50-bce3-7f18bf32a113','Cập nhật linh kiện sử dụng','2025-12-27 12:28:08.473951','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('84f45f52-d4ab-4015-91b8-f6b898cf251a','đang xử lí','2025-12-27 10:26:55.837349','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('87171a51-581f-461f-9a77-ae496e05f69a','Khách hàng xác nhận đơn sửa chữa','2025-12-27 09:55:26.570266','2078da1d-553c-4e88-95e5-8ec565e6e06d','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('88a40303-ebf6-400a-af1e-1a52da88f877','Cập nhật linh kiện sử dụng','2025-12-27 12:38:41.205855','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('97b4bbf4-9254-4a13-80bc-3d34d522dc31','Cập nhật linh kiện sử dụng','2025-12-21 11:19:52.555295','40c9f706-279f-4d88-b5c1-1813f4b75a7b','33ff2646-2922-416c-8216-c877201ed659'),('9fd0aef3-1a58-455b-9d8b-35ba2a1e2900','Cập nhật linh kiện sử dụng','2025-12-27 12:38:54.945546','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('a31891e6-dc69-47e3-a8d3-94b6327e38f4','2555','2025-12-27 09:54:41.766926','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('ab314db3-529f-46d3-996a-0c3ad8258c51','xác nhận','2025-12-27 09:50:39.625990','16bc5bc9-93fd-4619-aad9-ab1b3020b92b','33ff2646-2922-416c-8216-c877201ed659'),('ba22f883-99c0-4263-80c7-bcce2eb20c6f','Cập nhật linh kiện sử dụng','2025-12-27 10:40:11.167093','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('bce5ab95-ed5f-4dc8-a5d2-998f278c1c4d','Cập nhật linh kiện sử dụng','2025-12-27 09:53:37.269802','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('cc3ec101-08f4-48ea-bf1c-23dc10f8b98f','Cập nhật linh kiện sử dụng','2025-12-16 16:56:37.759303','40f4cac8-d02e-46d8-84bc-c71d13849c48','33ff2646-2922-416c-8216-c877201ed659'),('d0d4024a-ea7e-4a90-98b2-faff4a5d8c53','Cập nhật linh kiện sử dụng','2025-12-27 12:41:22.799955','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('d47ee51c-7995-40e6-a7e6-02676cba48d1','Cập nhật linh kiện sử dụng','2025-12-27 11:09:52.047277','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('e9b61ccb-e8e9-4b46-903a-070e8fc0ae16','Cập nhật linh kiện sử dụng','2025-12-27 12:41:08.652045','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('eeff519d-69ec-4273-bc84-4b3b45c5f041','Cập nhật linh kiện sử dụng','2025-12-27 12:39:14.942352','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659'),('fdfdb301-1e7d-437c-b2be-275aad0afb6a','lí do hủy','2026-01-05 12:21:03.600011','2078da1d-553c-4e88-95e5-8ec565e6e06d','33ff2646-2922-416c-8216-c877201ed659');
/*!40000 ALTER TABLE `order_note` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `order_at` date DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `status` int NOT NULL,
  `total_price` double NOT NULL,
  `type` int NOT NULL,
  `updated_at` date DEFAULT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKel9kyl84ego2otj2accfd8mr7` (`user_id`),
  CONSTRAINT `FKel9kyl84ego2otj2accfd8mr7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('16bc5bc9-93fd-4619-aad9-ab1b3020b92b','An Duong, Yen Phu, Tay Ho, Ha Noi',NULL,'test1@gmail.com','2025-10-27','0123123123',2,23690000,0,'2025-12-27','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('2078da1d-553c-4e88-95e5-8ec565e6e06d','	Ha Dong, Ha Noi','iphone 17 | sọc màn + pin yếu','tiendung01232003@gmail.com','2025-12-27','0123123123',4,11700000,1,'2026-01-05','ff5e7dc7-0c39-4b37-aba3-da4b267c88cf'),('32082142-3208-4c0f-bb69-2f9ca462ae56','An Duong, Yen Phu, Tay Ho, Ha Noi',NULL,'admin@gmail.com','2025-09-10','admin',2,30990000,0,'2025-12-13','6895cccc-4891-4c15-981a-e59a2d16a939'),('37036055-b58b-4cbb-9437-6aba043cd4ad','	Ha Dong, Ha Noi','iPhone 13 | chai pin ','','2025-12-14','0123123123',2,3650000,1,'2025-12-14','8460cfc0-ca09-4ce2-98bc-0fe5f6ba015b'),('39d3400d-a14f-413d-8151-5c2dade6065e','An Duong, Yen Phu, Tay Ho, Ha Noi','sssss','admin@gmail.com','2025-12-09','admin',5,30990000,0,NULL,'6895cccc-4891-4c15-981a-e59a2d16a939'),('40c9f706-279f-4d88-b5c1-1813f4b75a7b','	Ha Dong, Ha Noi','iPhone 13 | hư màn hình','','2025-12-20','0121212122',5,2450000,1,'2025-12-27','8460cfc0-ca09-4ce2-98bc-0fe5f6ba015b'),('40f4cac8-d02e-46d8-84bc-c71d13849c48','An Duong, Yen Phu, Tay Ho, Ha Noi','iPhone 11 | màn hình không lên','','2025-12-15','0123123123',5,1100000,1,'2025-12-16','6895cccc-4891-4c15-981a-e59a2d16a939'),('4eec3ad4-1290-431d-b619-e04a3174812c','Hn','giao hang ','adu112@gmail.com','2026-01-03','01234123413',4,21990000,0,'2026-01-04','6895cccc-4891-4c15-981a-e59a2d16a939'),('7f65266a-ba7f-4126-92f7-c81ebe72b981','	Ha Dong, Ha Noi',NULL,'admin@gmail.com','2025-12-16','admin',5,1350000,0,NULL,'33ff2646-2922-416c-8216-c877201ed659'),('994e8b08-ae0d-4989-b5eb-b21e6bd20b7c','An Duong, Yen Phu, Tay Ho, Ha Noi','iPhone 11 | sạc không vào','','2025-12-09','0123123123',2,0,1,'2025-12-14','33ff2646-2922-416c-8216-c877201ed659'),('9e29675c-e0e3-4bc0-9125-00b959b8097b','	Ha Dong, Ha Noi',NULL,'admin@gmail.com','2025-08-20','admin',5,79980000,0,NULL,'a1b2c3d4-e5f6-7890-abcd-ef1234567890'),('c16d490b-b60c-4b4a-88eb-dffa03edc386','An Duong, Yen Phu, Tay Ho, Ha Noi','iPhone 13 | chai pin, nhanh hết pin','','2025-11-14','0123123123',2,1150000,1,'2025-12-14','a1b2c3d4-e5f6-7890-abcd-ef1234567890'),('d5326a74-fae2-480f-9eba-6a5094484aaf','Ha Dong, Ha Noi',NULL,'admin@gmail.com','2025-11-15','0123123123',5,30990000,0,NULL,'33ff2646-2922-416c-8216-c877201ed659'),('debdd91c-c74c-4014-b963-2d507684efc8','Viet Nam','giao hang trong thang1',NULL,'2026-01-05','0111111111',0,24990000,0,NULL,'b78ae41e-2ec3-4065-a876-06a16a039f15'),('f7e36bc4-c0f5-453d-8190-be21bd11e698','Cua hang','khach hen cuoi tuan den ',NULL,'2026-01-05','0111111111',0,0,1,NULL,'b78ae41e-2ec3-4065-a876-06a16a039f15');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES ('MANAGE_PRODUCT','creata/update product data'),('MANAGE_ROLE','give roles for staff'),('MANAGE_STAFF','manage staff');
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `id` varchar(255) NOT NULL,
  `create_date` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `p_sku` varchar(255) DEFAULT NULL,
  `status` int NOT NULL,
  `category_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`),
  CONSTRAINT `FK1mtsbur82frn64de7balymq9s` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES ('09ca25bb-aa47-4562-b1dc-7635d3058b6d','2025-12-25','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679444/bej/products/dlhema5jmuxgdcg47qqf.webp','iPhone 16e - Chính hãng VN/A',NULL,1,10),('170fe367-601f-458c-a42c-72f6dbfd2c81','2026-01-01',NULL,'https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767277140/bej/products/vcviixebvcne9it6plpg.webp','Ép cổ cáp màn hình iPhone 13 Pro Max',NULL,1,33),('1a379678-5ba7-432b-b98b-7450a268eb52','2025-12-29',NULL,'https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012866/bej/products/yssjvdwanridzrxhsvib.webp','Ốp lưng iPhone 11 Pro Max Likgus trong suốt',NULL,1,12),('21eaa059-67e4-4651-bc2f-0556413ec539','2026-01-02','Thay thế tại cửa hàng giá +150.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767371818/bej/products/mnqb9kshszyihikapd2o.webp','Loa trong iPhone 13 Pro Max',NULL,1,24),('2b93e3b7-85d8-41a2-901b-bf9db01cc9fd','2025-12-14',NULL,'https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724039/bej/products/pgf98kqufzuz2awvq05f.webp','Chân sạc iPhone 11 Pro Max',NULL,1,24),('37b4cf27-a191-4086-ac99-61038beb729e','2025-12-07','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088451/bej/products/wxewkwpq0pbdrj8rjv4x.webp','iPhone 17 - Chính hãng Apple Việt Nam',NULL,1,10),('402b3525-d82d-41fb-99ce-a3f3478857e2','2025-12-25','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678758/bej/products/ymmquehluwsi9zpstn1t.png','REDMAGIC 10 PRO 16GB/512GB Bản Quốc Tế Chính hãng',NULL,1,10),('431fdadb-f3e1-4a62-ade1-9a9aea8e5e88','2026-01-03','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767438627/bej/products/tpbl6p1waaxvpiqjdn9h.webp','Cáp Pisen Quick - Mr White Type-C to Type-C PD 60w 1m',NULL,1,12),('4f6f678a-42ad-4608-9230-3675067835c2','2026-01-02','Thay thế tại cửa hàng giá +300.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340623/bej/products/jjbbldn0zu3vra3vgbth.webp','Vỏ iPhone 12 Pro Max',NULL,1,24),('4fcad8c8-ab27-4199-9844-a1f4ebce63b8','2026-01-02','Thay thế tại cửa hàng giá +300.000đ công thay, thời gian 1 ','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344305/bej/products/h6zro7dw0v8o7knsnuib.webp','Camera sau iPhone 12 Pro Max',NULL,1,24),('660a1160-c655-4fb8-8cde-8dee50a3c1be','2025-12-07','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725342/bej/products/e7rwa4ze1q0gs3vdpvlz.webp','Samsung Galaxy S25',NULL,1,10),('667ae8ff-b208-47b0-badc-94c8ef288f9f','2026-01-02','Thời gian 1-2h / Bảo hành 6 tháng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373606/bej/products/mekv5ko7kxhmgvkxswod.webp','Sửa lỗi xanh màn hình iPhone 13 Pro Max',NULL,1,33),('70e50eea-96cb-41db-a82b-96748afa49ac','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366397/bej/products/liuzfhulih9zeklmjjl4.webp','Ốp lưng iPhone 16 Aukey Magnetic Clear PC-TM20A',NULL,1,12),('7207c9f0-f3d0-4e51-b08b-5e3af6752ff6','2026-01-02','Thời gian sửa 1-3 ngày, bảo hành 6 tháng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767343852/bej/products/dasljkefe8ye3fl85l2m.webp','Sửa sọc màn hình Samsung S22 Ultra',NULL,1,33),('74f870ea-e771-4a36-8a2c-41a3c0cb328d','2026-01-02','Thời gian 1-3 ngày','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370440/bej/products/ynvxmnkbb60wpza8c41q.webp','Thay pin tai nghe Airpods 3',NULL,1,33),('80cf6e87-4abb-460b-8c40-fd5ae78b07ee','2025-12-25','Thay thế tại cửa hàng giá +300.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672079/bej/products/smxmrva2pc4ll8ujjtlw.webp','Màn hình Samsung S22 Ultra',NULL,1,24),('87da2a8b-fc49-4cb7-8d9c-8e1c0195b29a','2025-12-25','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675594/bej/products/qvl0cwzvlq8edoworrjc.webp','HONOR Magic V5',NULL,1,10),('8ef527d3-a6ff-446d-bc7f-9be965416d72','2025-12-25','Thay thế tại cửa hàng giá +100.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766677999/bej/products/gkdn2aiuwphty6nj6xwq.webp','Màn hình iPhone 7 Plus',NULL,1,24),('8f05354f-651e-484e-af65-3b8142bb3df7','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370681/bej/products/rm6bc1smyaofj9hq526r.webp','Ép cổ cáp màn hình Samsung Galaxy Z Flip',NULL,1,33),('8f518e4e-895e-4ab3-8f4a-5bbdab3d3a2a','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370172/bej/products/wbjyq6zrf8vx9to5fizo.webp','Củ sạc sạc Anker PD 1 cổng 30W A2017',NULL,1,12),('96b5e3c7-65a8-47fc-9046-3907208dac62','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366698/bej/products/yerb0rxv5vmnybq4kcuu.webp','Pin dự phòng Pisen Quick Powerlink Color 22.5W 10.000mAh kèm kép Lightning và Type-C D153',NULL,1,12),('a5345b35-ab10-49c0-8a5d-ed95c7b2b472','2026-01-02','Thay thế tại cửa hàng giá +250.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373290/bej/products/psnkwtkyh53cbxngoy8v.webp','Màn hình iPhone 12 Pro Max',NULL,1,24),('a66b3da2-41ff-4ab3-bc36-04ef10de2e8b','2025-12-29',NULL,'https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019844/bej/products/mti9q2ro121n8ecsrqcw.webp','iPhone Air - Chính hãng Apple Việt Nam',NULL,1,10),('bb0762fa-840e-4de1-adb7-d191ebe7d57f','2025-12-07','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724716/bej/products/vhv22cqkrwvoiuiwdta6.webp','Samsung Galaxy Z Flip7 5G',NULL,1,10),('d6cec6d1-e959-450d-b646-23de4258c0bb','2025-12-14','Thay thế tại cửa hàng giá +100.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701713/bej/products/cmrm46xt21juid58mfme.webp','Pin iPhone 11 Pro Max',NULL,1,24),('db5ecd60-639a-4ae3-9942-840254c92e79','2026-01-02','Thay thế tại cửa hàng giá +300.000đ công thay','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767364653/bej/products/p0btq4cg93clwgbtr54l.webp','Màn hình iPhone 14 Pro Max',NULL,1,24),('e34531ef-d5a9-4033-bbd8-10824e0d7cd5','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767368711/bej/products/pa5c7dzk8wzswuqmvinr.webp','Tai nghe có dây Baseus Encok CZ11',NULL,1,12),('f0d86718-f662-44b5-887a-df5f811fb027','2025-12-29',NULL,'https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012637/bej/products/wbsyfdajroolvpsjagcu.webp','Sạc nhanh Baseus Palm 1C1A 20W US ( kèm cáp mini C to C 60W dài 1m )',NULL,1,12),('f262fea0-9e58-4863-85bf-c80bd28e62fa','2025-12-07','Máy nguyên seal chính hãng','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112031/bej/products/wttxgjapgmknj8m1bgbx.webp','Samsung Galaxy Z Fold7 5G',NULL,1,10),('f432b71f-a20a-4e48-8e8e-bf823e7052d5','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344095/bej/products/efl10ooleokzldwwc69e.webp','Sửa sọc màn hình iPhone 13',NULL,1,33),('f7482c36-6446-45ea-bcb0-332676eb3729','2026-01-02','','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767342816/bej/products/b1ethwg6ayilz9avrtcp.webp','Sửa sọc màn hình Samsung S22',NULL,1,33);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_attribute`
--

DROP TABLE IF EXISTS `product_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_attribute` (
  `id` varchar(255) NOT NULL,
  `discount` double NOT NULL,
  `final_price` double NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `original_price` double NOT NULL,
  `sold_quantity` int NOT NULL,
  `status` int NOT NULL,
  `stock_quantity` int NOT NULL,
  `variant_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdxxu30j9j8nasm7w87624i9ku` (`variant_id`),
  CONSTRAINT `FKdxxu30j9j8nasm7w87624i9ku` FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_attribute`
--

LOCK TABLES `product_attribute` WRITE;
/*!40000 ALTER TABLE `product_attribute` DISABLE KEYS */;
INSERT INTO `product_attribute` VALUES ('0256c9b9-93c0-4be6-97fc-6bf0cc568142',0,1100000,'',1100000,1,0,1,'5394056d-3a8d-430c-a8ba-ac29459747ab'),('06dc2e64-b116-4607-9661-ab90fc775fbd',0,270000,'',270000,0,0,0,'af17f796-0768-46cd-8a2e-ac9a81ee8b82'),('0b783dd0-73f9-4e9d-b501-f68782a4f048',0,160000,'',160000,0,0,44,'81686a0c-a74a-419e-bff1-95bc906732ee'),('18126b58-abe8-4e52-8547-d0a075a82255',0,270000,'',270000,0,0,0,'c6084988-a857-437b-ba6e-913518022224'),('2120f8bf-6fb7-487a-b3b2-25b810746d1b',0,25990000,'256 GB',25990000,0,0,0,'a5eb4325-c62e-4069-8b87-e5e23423108b'),('22cc47ba-2d81-4fee-b411-78fc6de99fb1',0,99000,'',106000,0,0,22,'ebd39f8c-cac2-49b5-8bec-15230d09b342'),('22d8e5d9-1e3b-46e3-9c14-566dd40a71fc',0,450000,'',450000,0,0,0,'37fdfd6c-293d-4e76-8003-a7014ba36902'),('26cdc5a4-cffc-46de-bae2-3ebbfdcd68ac',0,38490000,'1 T',38490000,0,0,22,'a5eb4325-c62e-4069-8b87-e5e23423108b'),('27c5e7f7-5bae-4ef4-ae93-90ea01eee7f1',0,1600000,'Vỏ máy',1600000,0,0,2,'f4286275-3337-41fe-af41-f30cc4abf311'),('29daf916-6253-4426-b719-067b0c005f3b',0,3500000,'GENA loại A+',3500000,0,0,0,'bee3f0ca-0cae-4dc3-b0fc-71943df56ce1'),('2c3deffd-8672-40a8-b836-28aac21cc174',0,450000,'1 bên',450000,0,0,0,'531c1c6c-2adb-4f18-ae23-6e14591226b2'),('2ce4a4da-29b2-4eaf-bfe5-cee3e9bff66a',0,500000,'',500000,0,0,0,'597e3cbc-43ab-4b90-b9af-3ca3da861fce'),('2f6d8da8-0b89-41a1-8401-f14ef3bb3962',0,30990000,'512 GB',30990000,21,0,211,'9474de3c-3158-4724-abd8-1ceed03d6ec9'),('345de1a2-1d7c-49dd-8e6a-d0e250c24bc7',0,30990000,'512 GB',30990000,21,0,12,'bb5d5194-344e-4f7f-9386-c6671981f22b'),('358e5de6-b384-4a83-891f-1a40699bee17',0,31990000,'512 GB',31990000,0,0,21,'a5eb4325-c62e-4069-8b87-e5e23423108b'),('37c1a9e8-2664-4f54-a6e6-2d555d05712d',0,30990000,'512 GB',30990000,21,0,211,'1bbd8a48-ff12-4744-b7b6-58acdeae5940'),('3c567e5b-8f7e-486b-be05-9cb678e888fb',0,600000,'Kính lưng',600000,0,0,2,'43efa5ab-9bf7-4e4a-9a72-c3b797e6214d'),('3cadde1d-6e78-4e0d-a3ae-c7f7c5fbfe8d',0,900000,'2 bên ',900000,0,0,0,'531c1c6c-2adb-4f18-ae23-6e14591226b2'),('3d038e44-a3d4-48c6-ba45-b94f1d8dce43',0,24990000,'256 GB',24990000,221,0,212,'bb5d5194-344e-4f7f-9386-c6671981f22b'),('42729f79-72d2-43fd-82a5-5a7015895744',0,500000,'Kasr',500000,0,0,0,'6eeb68fc-19d9-49e3-86d4-72a431aa563a'),('44b8b065-5219-4e72-ae98-245c8ea633f3',0,25990000,'256 GB',25900000,0,0,22,'abc23749-9d07-4618-8fc7-21efbd02555b'),('4568371b-c21e-495f-a2ef-00cbe56585cb',0,1800000,'',1800000,0,0,0,'20d81f87-12fb-4eb1-b05e-298890d5dc07'),('47768f74-0d0f-49fc-9235-4c53bf7899eb',0,25990000,'256 GB',25990000,0,0,31,'2020408f-e503-42ca-997b-bf2474b15ca8'),('482c9c04-dc27-4100-9151-037cb14cea9a',0,23690000,'512 GB',25990000,0,0,21,'2f02e70e-c412-48a2-9e03-cf5525a7673e'),('4fbce593-df03-4331-969a-50c9b4c79579',0,800000,'',800000,0,0,33,'b32f254c-839e-4a32-906e-8043efe1d03e'),('5898f75f-61bb-463c-a202-233b74ab6238',0,31990000,'512 GB',31990000,0,0,43,'abc23749-9d07-4618-8fc7-21efbd02555b'),('59b33be6-8de2-4389-b0f4-b18130a21619',0,9200000,'GENA loại PRO',9200000,0,0,0,'bee3f0ca-0cae-4dc3-b0fc-71943df56ce1'),('5cc5ace4-8d6d-4592-8e34-526c971049b3',0,4600000,'GENA loại PRO',4600000,0,0,2,'63369aaf-f57f-4cf2-ad15-5f008b58b77d'),('5dabc04d-f222-4e26-84f9-aa49321e0293',0,25990000,'12 / 512 GB',31990000,342,0,3214,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f'),('5db6d8e6-9038-425f-8758-9e968b3ed05c',0,500000,'Kasr',500000,0,0,21,'17c27e02-97b7-4e30-9b2d-fcc19e2fef24'),('6246f096-1152-4968-b9a1-dc26c54c55ac',0,1500000,'',1300000,0,0,0,'2ba81e79-29e2-4a4c-ab71-f12508c0c25a'),('6282256a-fc87-473a-95ad-0a8697da011b',0,38490000,'1 T',38490000,0,0,33,'abc23749-9d07-4618-8fc7-21efbd02555b'),('6294ca85-c862-4456-8583-e7bd64de5652',0,17990000,'256 GB',22990000,33,0,2,'f4646957-1ba8-4b63-93ca-6ccdd4c32659'),('63f740ac-1f9b-4e19-b570-fbba807c2f41',0,459000,'',459000,0,0,0,'a2656cca-0b2a-4296-aae0-e72b412fc4f9'),('67cc11aa-95dd-4dbd-8ab1-1b167ffca1ab',0,17990000,'12 / 256',21990000,0,0,4,'c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf'),('68ea9fff-e232-4eeb-bee8-5c38cab66e51',0,930000,'GENA',930000,0,0,11,'6eeb68fc-19d9-49e3-86d4-72a431aa563a'),('6baa992a-2960-4668-80b2-8e7299738dbc',0,1000000,'Cơ bản',1000000,312,0,231,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2'),('6f9da57d-a875-4401-8f06-403675fb9798',0,590000,'',800000,0,0,2,'08133f27-866f-411a-b37a-ffd6e1e8ad0a'),('74060578-7be8-48ad-bae4-ad8f9f81b624',0,37990000,'12 / 256 GB',46990000,23,0,2,'57a1dea6-0216-4c60-9eb8-bbfc12647e17'),('76c49715-3865-47e4-b8a0-debd6cc9f846',0,31990000,'512 GB',31990000,0,0,2,'2020408f-e503-42ca-997b-bf2474b15ca8'),('85d39e5f-cddd-43d6-9530-ca7b69868ede',0,1550000,'',1550000,0,0,0,'c12c774d-7b13-4137-97ac-84f9b726743d'),('89d38da9-8f3f-41d0-9ccf-51dbb46e6fe2',0,1400000,'EK Pro Incell',1400000,0,0,0,'bee3f0ca-0cae-4dc3-b0fc-71943df56ce1'),('8b445633-492f-4c85-b0d8-4ce11f392e3e',0,38490000,'1 T',38490000,0,0,12,'2020408f-e503-42ca-997b-bf2474b15ca8'),('93741875-e57e-4027-9c45-1ba493bc1312',0,6600000,'màn rời',5000000,0,0,0,'23ab34b0-f3b2-4580-ada0-e51511003c75'),('94b89d34-2f7e-4ff5-bd61-7d64f7b1a897',0,1400000,'',1400000,0,0,0,'eac5da1f-40ef-416c-93b5-458fb04f7773'),('9806b29d-7956-4456-b730-fe9cca8955e6',0,150000,'',150000,0,0,0,'d1c4661e-e01e-44ac-9d32-a2199d1df2d3'),('9eb6b200-3f86-4ff5-a220-94b73104ee52',0,24990000,'256 GB',24990000,121,0,222,'1bbd8a48-ff12-4744-b7b6-58acdeae5940'),('9ebbb6ae-f4df-48c9-ad49-3135302fc362',0,42990000,'512 GB',42990000,0,0,22,'1febc89c-c8d7-483f-8914-fe5e5e87566c'),('a3c54274-956e-45ef-8a5c-989cb13ad3e0',0,22990000,'12 / 256 GB',28990000,23,0,2,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f'),('a4c9b790-feae-4218-b348-e3536c8cb1ca',0,0,'Vỏ máy',0,0,0,0,'43efa5ab-9bf7-4e4a-9a72-c3b797e6214d'),('a98d65bc-02fb-4fc2-a5ed-aed1d9f805c1',0,21990000,'16 / 512',23990000,0,0,3,'50d6b686-2de3-473d-b641-f8b2ed2f1834'),('b7b7dda1-d5c5-40d9-839e-37dd2cadec9d',0,41990000,'12 / 512 GB',50990000,432,0,2,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670'),('bb21dbbb-8264-4c2d-afbb-9c82a88545b3',0,5000000,'màu đen',4000000,0,0,20,'82e7d0d1-6949-4280-b004-aa509de94952'),('bb955a7b-b3cf-43f2-98ee-bfe7db48b7a3',0,200000,'',200000,0,0,0,'06a1affa-af1e-48f6-85ca-dfd08d55e711'),('bf555cff-fb14-49d5-b9b1-d95435472a6b',0,930000,'GENA ',930000,0,0,22,'17c27e02-97b7-4e30-9b2d-fcc19e2fef24'),('c0c5e0c9-6114-4988-bd67-d6520e7e4837',0,24990000,'256 GB',24990000,221,0,212,'73aea84a-350c-4dbd-9440-e31380f7d802'),('c295d403-fbaf-4f3c-bfb8-4b7e299f3872',0,1950000,'GENA loại A+',1950000,0,0,2,'63369aaf-f57f-4cf2-ad15-5f008b58b77d'),('c4af9cee-db4d-4d90-a916-d82da1ea7295',0,1200000,'Dung lượng cao',1200000,2313,0,123,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2'),('c5e053b1-d4d6-43cb-87b8-b1433f233bb4',0,15250000,'128 GB',16990000,0,0,43,'2f02e70e-c412-48a2-9e03-cf5525a7673e'),('c6bfd849-0006-48fe-9b85-f78c23a06000',0,17990000,'256 GB',22990000,2,0,2,'706b8987-aff6-4f11-a60b-047eca77ac7c'),('c9b22ab5-4ac4-43e4-9ea3-0f48b1fc2f6b',0,18350000,'256 GB',19990000,0,0,22,'2f02e70e-c412-48a2-9e03-cf5525a7673e'),('cbb6c151-2739-4ea0-ac1b-6a8fca460870',0,37990000,'12 / 256 GB',46990000,2,0,2,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670'),('cd3841c8-3587-4d0c-a237-779287830cfa',0,25990000,'12 / 512 GB',31990000,432,0,2,'e7df39fd-0db5-400b-868d-92bbaa275df3'),('d2cf5938-f8a8-45e3-8641-e35e206903f3',0,21990000,'16 / 512 ',23990000,0,0,31,'8280c8a4-b6b7-40b9-ad09-f48fabbfba93'),('d5d41a29-a951-442c-95bc-f400e17ed51c',0,1600000,'',1600000,0,0,0,'aa16f592-74f2-4bb4-8af2-745510847031'),('d6160e8c-c48a-4548-9acc-a4c93d6eb007',0,30990000,'512 GB',30990000,21,0,12,'73aea84a-350c-4dbd-9440-e31380f7d802'),('d71b0222-f133-4e32-a615-a25a13e30213',0,46990000,'12 / 512 GB',50990000,342,0,3214,'57a1dea6-0216-4c60-9eb8-bbfc12647e17'),('da60577a-378f-450e-83c1-b9e16a0cce39',0,459000,'',459000,0,0,0,'c23e1cdd-4adf-4fb3-afd6-d3aad91a0faa'),('daa4ebe7-573a-4427-9681-9b0c45e7b4c0',0,23690000,'512 GB',25990000,0,0,41,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb'),('e037220a-3c97-4785-8ea0-fa0b1be7c324',0,18350000,'256 GB',19990000,0,0,21,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb'),('e118f14b-899b-4aff-a1d9-38c4e51a5082',0,1200000,'EK PRO loại incell',1200000,0,0,33,'63369aaf-f57f-4cf2-ad15-5f008b58b77d'),('e74352f3-5029-40b6-a844-7c84133c1852',0,42990000,'512 GB',42990000,0,0,12,'50e7fc07-4964-440a-b28d-4483e2d1beb7'),('ebc1f706-059d-47a4-9816-cd0133b23fde',0,700000,'',700000,0,0,0,'f164ca5f-6240-473c-a175-575863b9bdd2'),('ec07984e-0354-4f40-88a7-8ebe8e76153e',0,100000,'',100000,0,0,31,'3ec17923-3c70-4ee5-8d8c-ca44362b4c8f'),('edf83bc3-356e-452b-bc90-2aacc6bb7378',0,15250000,'128 GB',16990000,0,0,12,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb'),('ee615086-eb98-452e-b630-81e7933636cc',0,1600000,'Vỏ máy',1600000,0,0,0,'27038b18-774e-404f-96a5-b7b990849aea'),('efb498b3-e120-4b9a-89f7-382a57728365',0,135000,'',100000,0,0,0,'57ed281c-3fd1-438a-a21d-a90f9fb54328'),('f06df638-6254-48a6-a8e8-6112bcffc301',0,24990000,'256 GB',24990000,121,0,222,'9474de3c-3158-4724-abd8-1ceed03d6ec9'),('f58a5375-b0b6-4fe4-97d1-fd0ca70be8bf',0,600000,'Kính lưng',600000,0,0,3,'f4286275-3337-41fe-af41-f30cc4abf311'),('f978f51c-7ba1-4d03-8e4d-ae67414c2d00',0,550000,'Kinh lưng',550000,0,0,0,'27038b18-774e-404f-96a5-b7b990849aea'),('ff42ee7b-cef7-4345-a74c-56e135b46400',0,22990000,'12 / 256 GB',28990000,2,0,2,'e7df39fd-0db5-400b-868d-92bbaa275df3'),('ff42fd0d-0a32-4e84-8f6e-f1c9bd1d6053',0,1150000,'Dung lượng cao',1150000,23,0,12,'c9e48711-ed3c-484d-8158-2cef705fb45e');
/*!40000 ALTER TABLE `product_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_image`
--

DROP TABLE IF EXISTS `product_image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_image` (
  `id` varchar(255) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `variant_id` varchar(255) DEFAULT NULL,
  `sort_index` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6oo0cvcdtb6qmwsga468uuukk` (`product_id`),
  KEY `FKe67ln3cxyqqfleou64mihh4gg` (`variant_id`),
  CONSTRAINT `FK6oo0cvcdtb6qmwsga468uuukk` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `FKe67ln3cxyqqfleou64mihh4gg` FOREIGN KEY (`variant_id`) REFERENCES `product_variant` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_image`
--

LOCK TABLES `product_image` WRITE;
/*!40000 ALTER TABLE `product_image` DISABLE KEYS */;
INSERT INTO `product_image` VALUES ('00ce159e-b090-43fa-9808-d03915ba115b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724730/bej/products/ukhgytx1unf9dvy9wwgg.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',0),('01c68645-3dc3-4665-b36b-25d9090d2257','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112046/bej/products/oyz4m7zboiww6iekm0w7.webp',NULL,'57a1dea6-0216-4c60-9eb8-bbfc12647e17',2),('027072f6-86dc-49ba-8d45-6a9e6a54e538','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725364/bej/products/hcpsdqb549mtuhhv0vnz.webp',NULL,'73aea84a-350c-4dbd-9440-e31380f7d802',0),('03821906-1f45-49be-bef9-a465373c0cd2','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725352/bej/products/t3nzzagucmqzt0ihnuhp.webp','660a1160-c655-4fb8-8cde-8dee50a3c1be',NULL,3),('03831190-779f-4a89-9474-4a2e555bd019','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019874/bej/products/yyhiuk8wlks2ym5tk58u.webp',NULL,'2020408f-e503-42ca-997b-bf2474b15ca8',2),('04f9bae9-3711-43b5-acf0-2e3a7a455233','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675613/bej/products/n64ibnrwibgjt2x9tocd.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',5),('05376208-174d-42fa-9a43-4b1b9d33e92f','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366413/bej/products/hok1axexvvrtqjaersov.webp',NULL,'81686a0c-a74a-419e-bff1-95bc906732ee',2),('05be8326-de3f-42b4-bd51-e5f56ba9b3de','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112033/bej/products/ptiszvywiku8a3re4ril.webp','f262fea0-9e58-4863-85bf-c80bd28e62fa',NULL,0),('05cf9356-0950-4c1a-bba0-734e820de136','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088470/bej/products/gxkjnfrh4przwxqsfzmk.webp',NULL,'bb5d5194-344e-4f7f-9386-c6671981f22b',1),('06f942a1-ca53-4eba-8d19-2d13f70d127a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725381/bej/products/haebtamibfpnovbzdel7.webp',NULL,'706b8987-aff6-4f11-a60b-047eca77ac7c',3),('078912c3-7917-49dd-935b-091fd4e377fb','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724050/bej/products/l5k43vphieqlpz50pryf.webp',NULL,'5394056d-3a8d-430c-a8ba-ac29459747ab',3),('08500da1-3c26-4a9f-b293-e1b53b157a37','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112050/bej/products/e8wk9xckhxptvsry2ku0.webp',NULL,'57a1dea6-0216-4c60-9eb8-bbfc12647e17',4),('089d86f1-8296-4d74-84be-b2ee513fd97b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767364656/bej/products/z19koaucsvmpfxqetiqh.webp',NULL,'bee3f0ca-0cae-4dc3-b0fc-71943df56ce1',1),('08b00286-f562-4de6-88d5-083915276113','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112039/bej/products/vripqri4p05nq603ipdc.webp',NULL,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670',3),('08b91c6d-74f0-48bd-9ac6-34e2157ea231','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019872/bej/products/iq0q94kdbomw8uq1yvqk.webp',NULL,'2020408f-e503-42ca-997b-bf2474b15ca8',1),('09caffdf-a1cc-4dba-a5de-300a89157c95','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725358/bej/products/kyt8yvazqy1bhjnakbwj.webp',NULL,'1bbd8a48-ff12-4744-b7b6-58acdeae5940',1),('0bdb2923-9eae-429e-a14b-309f727094f3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724044/bej/products/fmegswby3ropxvkfrm6u.webp',NULL,'5394056d-3a8d-430c-a8ba-ac29459747ab',0),('0f1690a1-526a-4412-afc4-ff13a765600d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344312/bej/products/ixlsqo9s4bqirzv6gqxk.webp',NULL,'20d81f87-12fb-4eb1-b05e-298890d5dc07',1),('0f8883a4-6bc7-40cf-b209-026d8075d758','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725348/bej/products/rgnuy4qsdu2t2icdfvre.webp','660a1160-c655-4fb8-8cde-8dee50a3c1be',NULL,2),('11d07d7a-e86a-4da0-b89f-45e7dbd8556a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725376/bej/products/verd5qatfpcswevepzhp.webp',NULL,'706b8987-aff6-4f11-a60b-047eca77ac7c',0),('14ec484d-e39d-4740-a33d-c59c62ed66ad','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672093/bej/products/z3m0k7xdfkrpiilo86kt.webp',NULL,'23ab34b0-f3b2-4580-ada0-e51511003c75',0),('156c9cf3-bc23-4fbb-be8d-977666c94587','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701954/bej/products/iksy5h26rxdvt7hcrwta.webp',NULL,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2',3),('168ee439-3b38-4fcf-b153-681ee447d1e2','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767342828/bej/products/rjrc22sfbuut7ymseox5.webp',NULL,'eac5da1f-40ef-416c-93b5-458fb04f7773',2),('172e6bfa-10b0-474e-88aa-7efab36abf21','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012643/bej/products/c0qhoxvhooii3yith54t.webp',NULL,'c6084988-a857-437b-ba6e-913518022224',2),('18020a6b-ecc2-431d-b22a-3d18d4c110e6','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725368/bej/products/kcijabveu0nd9uty4ufx.webp',NULL,'73aea84a-350c-4dbd-9440-e31380f7d802',2),('18124da5-7c9e-4c71-9f64-7e958031733e','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373616/bej/products/nnyvgkufopv7bfdgpugc.webp',NULL,'597e3cbc-43ab-4b90-b9af-3ca3da861fce',2),('19ab4bcb-5876-4d53-b9bc-48cd45bf9abf','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370186/bej/products/cwvymhid3obcpmbmr4kh.webp',NULL,'b32f254c-839e-4a32-906e-8043efe1d03e',1),('1c016ff5-bcc4-4334-b5e4-b76e724e6ebe','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340626/bej/products/a2kdamlrrwjyjnwwnaug.webp',NULL,'f4286275-3337-41fe-af41-f30cc4abf311',0),('1c5433e5-6a2a-4561-bcab-bba27c53bd54','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366738/bej/products/cpelacqfrqmrobiqgcp8.webp',NULL,'a2656cca-0b2a-4296-aae0-e72b412fc4f9',2),('1ca71f07-4450-4e71-8c66-5bbdc11c1cec','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678776/bej/products/kfvbyp1ongzfi7yzdi7h.webp',NULL,'50d6b686-2de3-473d-b641-f8b2ed2f1834',5),('1d8b906b-511a-4f3e-9acd-833e99218267','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088456/bej/products/i7jtttz8uimhquq5x5vi.webp','37b4cf27-a191-4086-ac99-61038beb729e',NULL,2),('20072aff-da57-4126-a7de-edf10aecd5b3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724734/bej/products/sfqcxjvtr6vmk3dimad8.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',3),('211d0d3b-7ec1-42d2-9614-4a22a6ae2ec1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724718/bej/products/hpfbaoycnhgyeqcacdeb.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',0),('232b4433-4a7e-4d03-a861-7be761b42c96','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725370/bej/products/z52tiowbwtpr5zqmxy3f.webp',NULL,'73aea84a-350c-4dbd-9440-e31380f7d802',3),('24f3d6d4-644c-4d81-9064-7a39e51bc131','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370189/bej/products/uxcnz2h9lpeqtlbobk98.webp',NULL,'08133f27-866f-411a-b37a-ffd6e1e8ad0a',0),('256d061c-b490-41a3-987c-42a47c41c4c5','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767368714/bej/products/zgmolzpqsucgharwlg6m.webp',NULL,'3ec17923-3c70-4ee5-8d8c-ca44362b4c8f',0),('25a3df83-dd9b-456d-9340-a28190e0fc91','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672091/bej/products/zqcbtv2jgmzjaxrxsncl.webp',NULL,'82e7d0d1-6949-4280-b004-aa509de94952',4),('2644a69b-9a1b-4cc8-890b-09c90a31f0d0','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344106/bej/products/ofpbcrucnmduyjrwvnin.webp',NULL,'c12c774d-7b13-4137-97ac-84f9b726743d',2),('26c9803e-8f4f-4b7d-965b-7699acf847dc','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366408/bej/products/dcnla47lvnlbc3h0xdb3.webp',NULL,'81686a0c-a74a-419e-bff1-95bc906732ee',0),('27676b89-83ec-4aa1-9d66-b0fb0638277c','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678908/bej/products/hscypcehcfd0qfvjrnz4.webp',NULL,'c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf',2),('2779f01c-540f-4d6a-ab05-26edbd47d707','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701714/bej/products/rvbrdhv88ezg6y7zfajh.webp',NULL,'c9e48711-ed3c-484d-8158-2cef705fb45e',0),('27af2081-ea03-4569-8ca0-aac4bcf36709','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701720/bej/products/sbsl5bwz5fntkxgv8vho.webp',NULL,'c9e48711-ed3c-484d-8158-2cef705fb45e',3),('2878b861-8b21-4511-8452-e7b3ff13ad35','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679461/bej/products/rsifgscblpniexmgdaqx.webp',NULL,'2f02e70e-c412-48a2-9e03-cf5525a7673e',5),('29a7e348-4b08-478c-980a-c7c08466ed35','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678007/bej/products/oghajqvj79kl9nwxk1xl.webp',NULL,'6eeb68fc-19d9-49e3-86d4-72a431aa563a',0),('2c572095-2a04-4269-bd66-c5f76607a34d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678771/bej/products/hsqkkvkkxt6t0nflvy1c.webp',NULL,'50d6b686-2de3-473d-b641-f8b2ed2f1834',2),('2cff674f-98f6-4b78-b98e-07f6552a5fd1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767438637/bej/products/iilxnci8o7xpk49raddo.webp',NULL,'ebd39f8c-cac2-49b5-8bec-15230d09b342',1),('2da6e68f-9bce-404d-8a27-3d41e387c6d1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675605/bej/products/wjnzfcojrvrw9kzchbsc.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',0),('303a054e-d3ae-46d4-ae7a-56425dc0e761','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675608/bej/products/fnfaqe2q1vvccrpxtaqc.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',2),('31819d09-366f-4ce0-8a6c-fae056d76a5e','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672084/bej/products/qgbw7xs3zzs1k7mep0ze.webp',NULL,'82e7d0d1-6949-4280-b004-aa509de94952',1),('330c4362-e494-40f7-9c56-7332d34f67ec','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675601/bej/products/moglsmufxkjccyyralom.webp',NULL,'1febc89c-c8d7-483f-8914-fe5e5e87566c',3),('350fd49c-a3fc-40ff-a9c1-5b0f21a58551','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725359/bej/products/xvywssrxpb13x7hsptmb.webp',NULL,'1bbd8a48-ff12-4744-b7b6-58acdeae5940',2),('356991a4-5d5b-430b-a997-320c9f897210','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725390/bej/products/iyiro5qwq4ukwnzk67do.webp',NULL,'f4646957-1ba8-4b63-93ca-6ccdd4c32659',3),('36620198-b967-488b-b52d-6b6f2adc691d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012646/bej/products/asjbl1vcye8zamsipge0.webp',NULL,'af17f796-0768-46cd-8a2e-ac9a81ee8b82',1),('36ede998-b853-429b-806d-c21b7b9c61a5','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112043/bej/products/sjxlk8ylvcvv0vynwceg.webp',NULL,'57a1dea6-0216-4c60-9eb8-bbfc12647e17',0),('37289279-1f83-493d-90b7-b129372c9275','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019863/bej/products/ejcvfb3a3frclg8fsb5r.webp',NULL,'a5eb4325-c62e-4069-8b87-e5e23423108b',1),('372b5cf5-23ab-4e2f-abaa-61bc8842be6d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767277143/bej/products/vnunhkfkhhzmfwinakj4.webp',NULL,'2ba81e79-29e2-4a4c-ab71-f12508c0c25a',1),('37a74238-8ce6-43bd-91a2-14076422cd66','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672095/bej/products/wdml3n1dw6spauxo8stz.webp',NULL,'23ab34b0-f3b2-4580-ada0-e51511003c75',1),('37f0f4b8-dde5-4e74-849d-5ea57da835b9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725380/bej/products/xvcfdyhvlxn3vcdpifiv.webp',NULL,'706b8987-aff6-4f11-a60b-047eca77ac7c',2),('38e5b8c3-239d-493c-9a5c-2bba70bfab39','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373609/bej/products/neeabcymjqmvnbuwklru.webp',NULL,'597e3cbc-43ab-4b90-b9af-3ca3da861fce',0),('38ee6efa-0763-4dee-bfcb-43f59ee2791b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675603/bej/products/lhts1edn405dgohogiav.webp',NULL,'1febc89c-c8d7-483f-8914-fe5e5e87566c',4),('394e1715-ca9a-448d-8c6e-14ceb53a1374','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725386/bej/products/sx6agemxri6h2qbkvtvn.webp',NULL,'f4646957-1ba8-4b63-93ca-6ccdd4c32659',0),('39e76ea9-57bf-49fe-b1e5-2d33181ea1f7','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701950/bej/products/psazpx3il6tpylofnr3u.webp',NULL,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2',1),('3d909e61-cfd9-4268-91fb-5a85c5bcd7af','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724733/bej/products/maamjt3hfmvuesb1bh91.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',2),('3e873715-de6b-464c-8599-2bfbdc10f85a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724711/bej/products/ttxtblyhbv7k6vxqv3uh.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',6),('40b07302-d11d-4ab4-aa55-e774fd144522','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678765/bej/products/gdv9xr2ikwwmlfaymssa.webp','402b3525-d82d-41fb-99ce-a3f3478857e2',NULL,2),('420e5eea-4001-4013-8181-7f7a3f6c43e5','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088468/bej/products/pgijxwlwo5f5yyrhn97y.webp',NULL,'bb5d5194-344e-4f7f-9386-c6671981f22b',0),('424c51ca-14f5-4998-80a3-50182a7aba22','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019865/bej/products/vvmtagjqgbjoah6tw43u.webp',NULL,'a5eb4325-c62e-4069-8b87-e5e23423108b',2),('42610d3e-a9a9-4fd5-8127-d5f671158469','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678775/bej/products/cnroixqyak3kooau1qmc.webp',NULL,'50d6b686-2de3-473d-b641-f8b2ed2f1834',4),('4273daf1-a6cd-4d46-bc71-56207f872c11','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112051/bej/products/ukqkiauwth6iwr3hmh0j.webp',NULL,'57a1dea6-0216-4c60-9eb8-bbfc12647e17',5),('429a982e-cbb7-4fd0-ab23-7aa7a2c9fd72','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088453/bej/products/efolm7xm1uyhfr3fpjum.webp','37b4cf27-a191-4086-ac99-61038beb729e',NULL,0),('42dc5bf6-d634-4ec3-9078-35f9b7bdc710','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725354/bej/products/ho2ptxpggjhrsjhfyk4t.webp','660a1160-c655-4fb8-8cde-8dee50a3c1be',NULL,4),('446fb12c-d018-49e6-a9b5-6b76ae97985b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344315/bej/products/i1ghgrkxsqqi5kqaptw4.webp',NULL,'20d81f87-12fb-4eb1-b05e-298890d5dc07',2),('449b2fa7-5902-4f88-9288-81f4a75637c4','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373303/bej/products/glphevp4gf8mbgq0qxt5.webp',NULL,'63369aaf-f57f-4cf2-ad15-5f008b58b77d',3),('464e95d0-015d-44a0-8d63-7e4d3b2470d9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088466/bej/products/gw5gutwjb5lm5iwiup6f.webp',NULL,'9474de3c-3158-4724-abd8-1ceed03d6ec9',3),('478b892d-d577-4fdd-a179-288934d41c87','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678909/bej/products/m1e6791uohsbogey5h7y.webp',NULL,'c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf',3),('4840140a-ea6b-4d44-bd96-e79348e5d60c','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701948/bej/products/dguqbwkz4qokfbffm2or.webp',NULL,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2',0),('49baf4cd-e6d7-416e-a255-bbd2ddb6922a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088463/bej/products/bkwytl9b4xjxx2htxbwt.webp',NULL,'9474de3c-3158-4724-abd8-1ceed03d6ec9',1),('4ad75517-b9d0-45af-96d5-aa84265cd0bf','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679473/bej/products/huxt4kfxgtyqtc48ticu.webp',NULL,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb',4),('4bed66e4-fcec-49ab-9ddc-e34bdfefe6e9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678782/bej/products/tvgw5xl3orhny54l2nd9.webp',NULL,'8280c8a4-b6b7-40b9-ad09-f48fabbfba93',2),('4d019d23-28bf-4ab5-acc3-5334e0abe54b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724722/bej/products/fx8bim5usvmblqnvtx2m.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',2),('4fa220d5-a335-41e8-9b41-987641f57d3a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725375/bej/products/rqbql6hj5sq4851r9oxl.webp',NULL,'73aea84a-350c-4dbd-9440-e31380f7d802',5),('4fced4bc-58a9-4199-ac1e-eb1741fc4edb','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725393/bej/products/zjyq95tjwmewnjndrw7d.webp',NULL,'f4646957-1ba8-4b63-93ca-6ccdd4c32659',4),('520c7107-858a-404f-b874-9ceb3a3f2e43','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366710/bej/products/xethmsjmzbynd5k74for.webp',NULL,'c23e1cdd-4adf-4fb3-afd6-d3aad91a0faa',1),('5419d345-1287-4398-9757-14ea9b900a5c','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019877/bej/products/er7ou3vmgygh1yq4ik1b.webp',NULL,'2020408f-e503-42ca-997b-bf2474b15ca8',4),('565fe618-b66f-4faa-aeec-1e7123eb66d3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019859/bej/products/vh7aiwv4amp2aotx08zj.webp',NULL,'a5eb4325-c62e-4069-8b87-e5e23423108b',0),('58fe5e6c-7def-4ba2-9afe-0adf7305bfbe','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725362/bej/products/hkbxhvgcnv5lwvspagqg.webp',NULL,'1bbd8a48-ff12-4744-b7b6-58acdeae5940',4),('5b675b15-4a31-4230-b42f-5211382e8e8c','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701956/bej/products/zbwzq2tjtqc6jlomf0ua.webp',NULL,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2',4),('5cbf2b9a-6717-48b0-96f6-e0d3444be000','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724724/bej/products/aky2wsp6bji9dlpuxe8g.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',3),('5d389ba5-1501-4f23-a685-f3f25b65bc99','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370690/bej/products/bt5ubw1apjqtmjzmg8mu.webp',NULL,'37fdfd6c-293d-4e76-8003-a7014ba36902',0),('5fb5e93b-3615-41ea-a001-f61a3c2c5559','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370443/bej/products/i2hhcmjru9xlngqdvmcc.webp',NULL,'531c1c6c-2adb-4f18-ae23-6e14591226b2',0),('61b18710-4269-4400-9b87-aae1b6185bff','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767364658/bej/products/sx3kuecysaxeqb7disi6.webp',NULL,'bee3f0ca-0cae-4dc3-b0fc-71943df56ce1',2),('6652dfd8-e475-4141-864a-a3d919a9ce5a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701722/bej/products/zbhask7rrel9z7rn4pzg.webp',NULL,'c9e48711-ed3c-484d-8158-2cef705fb45e',4),('668bbeea-efef-408d-9f01-98482a47bd20','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019847/bej/products/xrrj3auy0rgintjvvwhv.webp',NULL,'abc23749-9d07-4618-8fc7-21efbd02555b',0),('6702baf6-eddb-46e1-a0b9-4d1da00b0022','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679447/bej/products/m0sibjquicca40j0gpgj.webp',NULL,'2f02e70e-c412-48a2-9e03-cf5525a7673e',0),('6a56b1f4-7158-4ab3-a1b4-fe8b1a378b09','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340635/bej/products/ykmtqprzmfjri7gu3wcv.webp',NULL,'43efa5ab-9bf7-4e4a-9a72-c3b797e6214d',1),('6b8b4156-c4cd-4f7a-b26d-142870e97601','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678785/bej/products/d0jzokxlhdofp7sr0q9m.webp',NULL,'8280c8a4-b6b7-40b9-ad09-f48fabbfba93',4),('6c1aa278-bad3-44dc-9373-bceaeaf25614','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019875/bej/products/bevgwwg15mva9wij8eaa.webp',NULL,'2020408f-e503-42ca-997b-bf2474b15ca8',3),('6c3a729d-6ae6-49a1-93a8-56b66a52b61d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012641/bej/products/rd0jw1p4jbnmuyikntip.webp',NULL,'c6084988-a857-437b-ba6e-913518022224',1),('6c572be5-1bec-4bee-8aa8-2459bcd6e734','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724727/bej/products/tkmoqms7cheofhmaej0e.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',5),('6c829aa4-1cdb-48bf-9fa3-17b04922e483','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767371825/bej/products/opphkvljcjdbpplhy3ee.webp',NULL,'f164ca5f-6240-473c-a175-575863b9bdd2',1),('6d667a7b-d537-4e48-af75-6eab4a2c2f69','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724048/bej/products/bfmkzathexig9jqqgmvu.webp',NULL,'5394056d-3a8d-430c-a8ba-ac29459747ab',2),('6eaed068-5732-4c8f-b3f9-fe916c61591d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767343855/bej/products/zc3fzp27gddeqrgaoieq.webp',NULL,'aa16f592-74f2-4bb4-8af2-745510847031',0),('7009a648-cca5-499d-abf7-8ef1e0c37518','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370447/bej/products/cpzofc83z7gpfgggjqnf.webp',NULL,'531c1c6c-2adb-4f18-ae23-6e14591226b2',1),('7166b075-a671-4dcc-9a3d-1e9591a659e9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088467/bej/products/ojqbct0eyzckc7ndrog5.webp',NULL,'9474de3c-3158-4724-abd8-1ceed03d6ec9',4),('72a2a62b-e319-4436-a4f6-9a1f485cf3c1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370193/bej/products/yxp3t3rzlhfngtxo3xwt.webp',NULL,'08133f27-866f-411a-b37a-ffd6e1e8ad0a',1),('73663090-8015-41b0-bb27-ea72813f7e1a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370178/bej/products/oukij4tama0sbs1hfzbz.webp',NULL,'b32f254c-839e-4a32-906e-8043efe1d03e',0),('74880990-4231-492c-a500-d491473682f9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366702/bej/products/mjkn4nclmcdjgpvspu7s.webp',NULL,'c23e1cdd-4adf-4fb3-afd6-d3aad91a0faa',0),('753e9d32-b77f-4db2-beff-67383930f252','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672082/bej/products/nxjabtagzhqdeeni338u.webp',NULL,'82e7d0d1-6949-4280-b004-aa509de94952',0),('757f6715-bcd1-43ab-a9d8-445eb8e68be7','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675599/bej/products/qdgmpr7nyp6qv5pmtqrp.webp',NULL,'1febc89c-c8d7-483f-8914-fe5e5e87566c',2),('759d6828-4c8a-45c3-8eda-fd2c48ed24e2','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370206/bej/products/mesb6iqbvyptvfptzyrd.webp',NULL,'08133f27-866f-411a-b37a-ffd6e1e8ad0a',2),('7771b576-9033-4315-813f-955bd245b9d1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112037/bej/products/qfckvvxzhukfvw9fizce.webp',NULL,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670',2),('77bccb30-5ca4-4ec1-b0d8-cf6dcaa631ee','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088454/bej/products/c8cbfyx1j47jhbuar5dt.webp','37b4cf27-a191-4086-ac99-61038beb729e',NULL,1),('79d378a1-a161-4fbb-83a9-392af2e2602a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767371822/bej/products/jrijsyrqhgzkgnsab2pi.webp',NULL,'f164ca5f-6240-473c-a175-575863b9bdd2',0),('7c0f93cd-7ad6-4b4c-9a94-a9843e30a9f3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344099/bej/products/ojwlwko6sf7r92emumzd.webp',NULL,'c12c774d-7b13-4137-97ac-84f9b726743d',0),('7c18b00e-4c53-4085-a66c-428f2ae22f66','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725360/bej/products/ber0lxjwhznkq4vzfljz.webp',NULL,'1bbd8a48-ff12-4744-b7b6-58acdeae5940',3),('7c6db166-c36b-4b6a-96cd-8bf64645152f','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373297/bej/products/cfhqxr1dfsqvyju5wyac.webp',NULL,'63369aaf-f57f-4cf2-ad15-5f008b58b77d',1),('817966f0-57de-41ff-b0d1-b96e5433b295','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725345/bej/products/hbaabx1vqfpkelaeaems.webp','660a1160-c655-4fb8-8cde-8dee50a3c1be',NULL,0),('8209a5ba-7605-4d75-93a8-e1f89991c7f9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725363/bej/products/bqq2oz4ncmshxaqbtarl.webp',NULL,'1bbd8a48-ff12-4744-b7b6-58acdeae5940',5),('82184745-2a59-4fe8-8d5e-1bf8e60a6210','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675597/bej/products/zaeorjbr2obsxfkn0mmz.webp',NULL,'1febc89c-c8d7-483f-8914-fe5e5e87566c',1),('84c5ea3b-7ded-48fd-9ecf-b8382055770a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679465/bej/products/fgqvgxoatggbhf2zmcxn.webp',NULL,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb',1),('8540ae12-3cd6-46d8-9bd7-6da27e803d76','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767438633/bej/products/hmcd9wy4d60skjurbfbe.webp',NULL,'ebd39f8c-cac2-49b5-8bec-15230d09b342',0),('85908dc2-6021-41bc-9ef6-4499e2844ed7','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012867/bej/products/orph6l3mojstutty2axr.webp',NULL,'57ed281c-3fd1-438a-a21d-a90f9fb54328',0),('876a304f-1079-4971-a7a5-eee10f09ae1d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012645/bej/products/zmqytdnlochzfnmagxar.webp',NULL,'af17f796-0768-46cd-8a2e-ac9a81ee8b82',0),('883f03d3-7303-4fe9-a3cf-46fa05b1f554','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340632/bej/products/zrp4qkol5hd1zh0bxvsl.webp',NULL,'43efa5ab-9bf7-4e4a-9a72-c3b797e6214d',0),('88ebd97f-8829-4de2-84ab-4e42012f5616','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678784/bej/products/nhdd887qpswvyzivsqm3.webp',NULL,'8280c8a4-b6b7-40b9-ad09-f48fabbfba93',3),('891317b5-f271-4739-ac13-51da18cc14b5','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679450/bej/products/icd700bxjsdwkfasvyrp.webp',NULL,'2f02e70e-c412-48a2-9e03-cf5525a7673e',2),('896c1861-4a3c-4ece-950b-99c48302a428','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344308/bej/products/jwcdbb6520nqfup436ph.webp',NULL,'20d81f87-12fb-4eb1-b05e-298890d5dc07',0),('8c9dc5b5-d178-419e-ba0a-009d0c974eb4','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725388/bej/products/v97waoilofnauisowzac.webp',NULL,'f4646957-1ba8-4b63-93ca-6ccdd4c32659',1),('8d64a750-3485-495f-9882-bbd87a7cccf9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701716/bej/products/r2qoyufcwx72hmkasz7c.webp',NULL,'c9e48711-ed3c-484d-8158-2cef705fb45e',1),('8dcf73ab-891f-4056-b5d9-935681905e44','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678769/bej/products/bicbxsdhs6v9cpzcwx0j.webp',NULL,'50d6b686-2de3-473d-b641-f8b2ed2f1834',1),('8f955373-35bc-46f2-b2a0-e5c336c06b17','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112040/bej/products/gi7olscfjtbuzfult5xv.webp',NULL,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670',4),('8f9d15e6-f103-40ba-b53c-0b405c6f4fcf','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340638/bej/products/ibatrqogmygx80fk8bz5.webp',NULL,'27038b18-774e-404f-96a5-b7b990849aea',0),('8fdabc4d-c6b1-4efa-b4e8-68f07f0f5e72','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767343860/bej/products/okwelxeweadeuzkwbojf.webp',NULL,'aa16f592-74f2-4bb4-8af2-745510847031',1),('91a6fd3c-3775-4cdb-8328-2c2c8ddc2c06','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019866/bej/products/hzkh6gh7il8hidmmaci8.webp',NULL,'a5eb4325-c62e-4069-8b87-e5e23423108b',3),('946ad13e-4906-4b5a-b21a-3b87c93020b2','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725383/bej/products/e6khnroqwbcjxsxdcaul.webp',NULL,'706b8987-aff6-4f11-a60b-047eca77ac7c',4),('94caa173-de8c-4b46-9f65-bff932322e11','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112042/bej/products/n2svn2zsf31uobnlyhin.webp',NULL,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670',5),('956869d2-4192-40df-99b3-d32d80b4f315','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112036/bej/products/clb7xoqkvihcnu61m2yg.webp',NULL,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670',1),('963146a6-1afc-42bc-b068-e52361a7ffaf','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088464/bej/products/g5dethcvxc5vjw4fdl42.webp',NULL,'9474de3c-3158-4724-abd8-1ceed03d6ec9',2),('991d561d-1873-4ada-b76f-041afcc8e279','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767277142/bej/products/mqm7kwl4sg0lu4ehcsus.webp',NULL,'2ba81e79-29e2-4a4c-ab71-f12508c0c25a',0),('99390d50-7219-475e-83d9-b849bd74ca1d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701718/bej/products/tqbpccjrefxmdqh9mmrk.webp',NULL,'c9e48711-ed3c-484d-8158-2cef705fb45e',2),('9b656dec-3413-4b86-a880-498a01ea672d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675611/bej/products/pgimssbn7mkimorysgxp.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',4),('9c8d3514-5139-4b16-8b25-ff8dd9214687','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679448/bej/products/aw9jbyy3klpow2fbx7iz.webp',NULL,'2f02e70e-c412-48a2-9e03-cf5525a7673e',1),('9f6ebaa4-d9ad-4fd0-b395-1b1b326a08c4','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012869/bej/products/xtlfglhun5bexgnzcg09.webp',NULL,'57ed281c-3fd1-438a-a21d-a90f9fb54328',1),('a067775f-c994-4af8-b03e-86c0477f5dab','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088461/bej/products/jmb29catynsrijxvcm9f.webp',NULL,'9474de3c-3158-4724-abd8-1ceed03d6ec9',0),('a0cd696f-444e-4fea-83ef-23e39f48ef0d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767344102/bej/products/k9vpdpyizvy7xpqfc6xm.webp',NULL,'c12c774d-7b13-4137-97ac-84f9b726743d',1),('a134a5aa-c65d-4422-87f1-4cd3021432d1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678773/bej/products/qdkritwg25wzlrtr6nps.webp',NULL,'50d6b686-2de3-473d-b641-f8b2ed2f1834',3),('a33976e7-75ec-4648-a74c-22e24080f5c8','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112034/bej/products/fppbdhbrzy7xloxynuga.webp',NULL,'ccb1f63d-8fcb-4ca3-bef7-56669ee30670',0),('a46b6626-e641-4ceb-8735-a598977afaea','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678763/bej/products/vifhq8kat3mrn7wttvk6.webp','402b3525-d82d-41fb-99ce-a3f3478857e2',NULL,1),('a866b3af-5a97-44a8-b3cd-7fdc7e47fe7a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366410/bej/products/fmwfcbwpa7kjilcewrkz.webp',NULL,'81686a0c-a74a-419e-bff1-95bc906732ee',1),('a8ff8513-03cb-4c01-b8a5-4f5d1e796c40','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678767/bej/products/zruz1ivztchu7bcxfydd.webp',NULL,'50d6b686-2de3-473d-b641-f8b2ed2f1834',0),('a946506e-f76e-445d-836c-bfd5176f473a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725378/bej/products/fx3wtzdut2edkck7qn2o.webp',NULL,'706b8987-aff6-4f11-a60b-047eca77ac7c',1),('aa393a24-639f-4fee-b5e3-0cbc57260c89','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088458/bej/products/amxqh207tn56c4q9rfmg.webp','37b4cf27-a191-4086-ac99-61038beb729e',NULL,3),('ab7b22df-4aff-496f-8224-ef9f07430b22','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370451/bej/products/d3gzmb5yalhlqxrljm7k.webp',NULL,'531c1c6c-2adb-4f18-ae23-6e14591226b2',2),('abc1ec30-eab8-4631-a1b0-7154cc6058ff','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675596/bej/products/hwflgxgtzy9hcfcrmpxm.webp',NULL,'1febc89c-c8d7-483f-8914-fe5e5e87566c',0),('ad4711eb-e6dd-428a-a0a8-56ff503e9793','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767342822/bej/products/jqpvfxigscls2un2vqyu.webp',NULL,'eac5da1f-40ef-416c-93b5-458fb04f7773',1),('aed4ed67-420d-45dc-92ed-10c182d9b15f','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679475/bej/products/og5jjpt23z3puusdynwf.webp',NULL,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb',5),('aeedc964-ee49-46d2-b347-48c94bcc886b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366714/bej/products/bgb87ws5us5iizsos7mm.webp',NULL,'c23e1cdd-4adf-4fb3-afd6-d3aad91a0faa',2),('b03d65c6-0446-4ce7-b138-50b7f96009ce','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724730/bej/products/cjbmjogsy4lk1zvy1syq.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',6),('b092ebd7-6595-4c78-b658-09d074c51f5f','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088475/bej/products/ipe0gzt1nrunhsvlkz18.webp',NULL,'bb5d5194-344e-4f7f-9386-c6671981f22b',4),('b0c57a29-8740-439b-bef8-472a7bab075b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373612/bej/products/nyvfsitt2zrnwojo3urz.webp',NULL,'597e3cbc-43ab-4b90-b9af-3ca3da861fce',1),('b0c7053e-296b-4b68-822f-e97120faf1cc','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679457/bej/products/xbm2vz6n3wxhmmvnzrlo.webp',NULL,'2f02e70e-c412-48a2-9e03-cf5525a7673e',4),('b0cc713f-f955-47b9-8d24-132f2f709bc3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724738/bej/products/bmll7qhikg9twzd8sicj.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',6),('b22cc5aa-9847-41c8-91ee-910845a1a051','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767364655/bej/products/ybdqixgjhtbsytr2sggy.webp',NULL,'bee3f0ca-0cae-4dc3-b0fc-71943df56ce1',0),('b30c867c-c1b6-49b2-8289-5510cf151897','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701957/bej/products/mp1del6ftd7bjnlzyhzu.webp',NULL,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2',5),('b6934d8e-171b-4183-a121-d950b3a25298','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019850/bej/products/qwmun1ytqa9geca1zrkd.webp',NULL,'abc23749-9d07-4618-8fc7-21efbd02555b',1),('b792f021-bc82-4d13-961e-ae974d242be3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679470/bej/products/ies3ietqjuiwtolgt49e.webp',NULL,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb',3),('b846898c-7325-4730-95db-7db3b35fa584','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340629/bej/products/plmdv1edh9az0h4glwqg.webp',NULL,'f4286275-3337-41fe-af41-f30cc4abf311',1),('b9b1f169-e008-446f-b058-b0796978b3b6','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725347/bej/products/irqlvpgtftxwojob4gt8.webp','660a1160-c655-4fb8-8cde-8dee50a3c1be',NULL,1),('bc4b525d-ca1a-4c86-9480-82d91470f960','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019853/bej/products/dvudtwicaxostiv7jknd.webp',NULL,'abc23749-9d07-4618-8fc7-21efbd02555b',2),('be383c3f-948d-4df9-9312-edc800ec42e7','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675615/bej/products/onkiu8hpkc1zzxdiabml.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',6),('be6d45a5-2c69-449c-853b-77b7231b6846','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724731/bej/products/jj06vz7by1qgtuchkhde.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',1),('be7bc228-4b8e-4d6c-9b9e-341a0013f374','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724736/bej/products/mqdslzq7c5gjxocsbx3x.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',4),('bfe84098-a5f2-4bd8-b166-4797d2931c23','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675609/bej/products/ycjaxgdbxhqboa1ryxwq.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',3),('c2fddc24-4230-4416-87aa-7a1af00215b0','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725384/bej/products/fb9m0whrlyuogmsxmhk0.webp',NULL,'706b8987-aff6-4f11-a60b-047eca77ac7c',5),('c3c42331-9471-4838-a3e7-2f207aa4105f','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766675606/bej/products/c90ciujtgbgacbedqgve.webp',NULL,'50e7fc07-4964-440a-b28d-4483e2d1beb7',1),('c5b84c76-96ec-4828-a82e-f31d20dafa4b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678005/bej/products/dwids7rar08jo2mtdvb0.webp',NULL,'17c27e02-97b7-4e30-9b2d-fcc19e2fef24',2),('c6d1925d-1e3d-4ea2-91d4-ec70e8e55aa3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678780/bej/products/zc3vvbzuxzsmbh9lj9dq.webp',NULL,'8280c8a4-b6b7-40b9-ad09-f48fabbfba93',1),('c6f89c06-b50a-4123-aae6-e23633357b2e','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725366/bej/products/ymkbmblml3eraaoacdbr.webp',NULL,'73aea84a-350c-4dbd-9440-e31380f7d802',1),('cd612533-a5ee-4593-bee2-c02afc477556','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679452/bej/products/x7fj0kocz8wxnx6ojajj.webp',NULL,'2f02e70e-c412-48a2-9e03-cf5525a7673e',3),('ce8f5861-ddb5-449c-bb9c-0a30f75f6da6','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012639/bej/products/wjm7t2engufiu9twoult.webp',NULL,'c6084988-a857-437b-ba6e-913518022224',0),('cfac0dbf-eb15-4e1d-9785-7fc6841fca67','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725389/bej/products/zswgjwxfqnwekyzjwy6t.webp',NULL,'f4646957-1ba8-4b63-93ca-6ccdd4c32659',2),('d26456aa-b175-4217-a5a9-7ebb375efb8a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678911/bej/products/inadgcrvnzhqe964ck40.webp',NULL,'c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf',4),('d2c581e7-8f8f-4314-ac25-698221e06e41','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366728/bej/products/dhvevdpeczoo1ykkolrv.webp',NULL,'a2656cca-0b2a-4296-aae0-e72b412fc4f9',1),('d4ad4d92-fdb2-42b3-997d-518a3da2723e','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678905/bej/products/f2bjqnsdailkldcvqr5a.webp',NULL,'c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf',1),('d4e40823-9294-4d3a-8dad-6027e8dc43b5','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724725/bej/products/c45hgovqhkiingge4a4s.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',4),('d53547e0-4dc6-4665-8042-3993ec9e72d2','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019855/bej/products/gyy2px6ktbiohpbvqcu6.webp',NULL,'abc23749-9d07-4618-8fc7-21efbd02555b',3),('d5a21b89-3328-40cc-bf89-b6f4489d4756','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765701951/bej/products/ldcyxdtijysgy5z3rdpj.webp',NULL,'1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2',2),('d71ee07e-1f51-4494-9ef8-426f4490f448','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678778/bej/products/ff5ctwv3tm4cwxwrxwp8.webp',NULL,'8280c8a4-b6b7-40b9-ad09-f48fabbfba93',0),('d75d8931-a93a-429f-af69-99fdf213d9ed','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724046/bej/products/gnv41yciqz2arr8mgal2.webp',NULL,'5394056d-3a8d-430c-a8ba-ac29459747ab',1),('d8f6ec2f-d7ac-4454-bbad-9104eeb28fb1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724737/bej/products/hy7nt2mtlq94lkloghvh.webp',NULL,'9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f',5),('d8fbd3e4-3f3b-4681-9d92-99c60e032e3c','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088460/bej/products/slanysokjimf3rpevesk.webp','37b4cf27-a191-4086-ac99-61038beb729e',NULL,4),('d990d9fc-0fbb-433f-a80b-8af4dbd12c06','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088473/bej/products/s9hyjnxiqjxbi62nv0le.webp',NULL,'bb5d5194-344e-4f7f-9386-c6671981f22b',3),('daede70b-36f1-4a49-8dbf-b6137df819b5','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767366719/bej/products/g2tv7otimccnoftubnql.webp',NULL,'a2656cca-0b2a-4296-aae0-e72b412fc4f9',0),('dd5c12ee-9b8e-460e-8365-beadfb923e5f','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767343864/bej/products/zswzzraeaa1vy9vrph4n.webp',NULL,'aa16f592-74f2-4bb4-8af2-745510847031',2),('dd9ea5dd-e166-4e3b-aa11-72f0d132959a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672088/bej/products/jcwv1qc9pwlscer59aqc.webp',NULL,'82e7d0d1-6949-4280-b004-aa509de94952',3),('de1e7c32-dcbd-44c9-82fe-d5f3552afff6','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767370699/bej/products/uh4cy6lso02q7rlvf402.webp',NULL,'37fdfd6c-293d-4e76-8003-a7014ba36902',1),('df5ec18e-3b69-42be-a73e-960f1d6f8ca1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019869/bej/products/tmzstux4lo5f6ybv007b.webp',NULL,'a5eb4325-c62e-4069-8b87-e5e23423108b',4),('df7d5181-dae9-4872-8b00-2df161883b7b','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019871/bej/products/ljhpndkhq8do0tdtskox.webp',NULL,'2020408f-e503-42ca-997b-bf2474b15ca8',0),('e246e212-2c83-4d97-b970-8331c14353b7','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767342819/bej/products/p2tuiwqkghzaftzevwpz.webp',NULL,'eac5da1f-40ef-416c-93b5-458fb04f7773',0),('e28f64c1-cea4-4e22-99f5-5ea3cd93d0ec','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678762/bej/products/jvwjrpkbvruiq6o7eatg.webp','402b3525-d82d-41fb-99ce-a3f3478857e2',NULL,0),('e3688c05-0dac-44d2-a777-959399e322ab','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765088472/bej/products/sgs4ztgwcm4n5rtjeg8w.webp',NULL,'bb5d5194-344e-4f7f-9386-c6671981f22b',2),('e3d055ab-bfd9-44e1-a614-d3e20bc721fe','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012648/bej/products/qbrmzi6nkavlvjvns5ot.webp',NULL,'af17f796-0768-46cd-8a2e-ac9a81ee8b82',2),('e5d6e7cf-d5c0-45f9-89d4-d0c5816c2147','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373300/bej/products/mqnhkcfjvuwq263rgigw.webp',NULL,'63369aaf-f57f-4cf2-ad15-5f008b58b77d',2),('ea9293c5-497f-4950-a4ae-d68e4a21d208','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679463/bej/products/bqz5cgqebieuhaay8z0r.webp',NULL,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb',0),('eb46a301-8acf-430a-8c50-fb597ea543d7','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724720/bej/products/okzoapw8hk8dghau8spx.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',1),('ebb90faf-ac58-4240-8bcd-3e656499cd55','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767340640/bej/products/sekhtdiemqxiwy7kdehi.webp',NULL,'27038b18-774e-404f-96a5-b7b990849aea',1),('ec0f382e-9df0-42fd-8efd-f2efaafedd1c','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725372/bej/products/xwjt3aqbmm64hudtxctg.webp',NULL,'73aea84a-350c-4dbd-9440-e31380f7d802',4),('ec2311db-caa3-4e6e-9935-c8f2cd757f30','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678010/bej/products/wre3vynddrtago29yhrp.webp',NULL,'6eeb68fc-19d9-49e3-86d4-72a431aa563a',2),('ee127a97-ad17-41ce-9c60-40c4aefdba47','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767373293/bej/products/jlpa6niftvzpc6s7c1lj.webp',NULL,'63369aaf-f57f-4cf2-ad15-5f008b58b77d',0),('ee58c181-fb29-4228-940d-946e414fd2f3','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765724728/bej/products/gxd7vxpyabnx0yd29brp.webp',NULL,'e7df39fd-0db5-400b-868d-92bbaa275df3',6),('ee8d9a4e-9e98-409e-a993-055b6edae67e','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766679468/bej/products/uxfulmlyfmbcv46e1rrk.webp',NULL,'c1c170d1-0cf0-4e9d-be54-58db2fe70ebb',2),('f4b1090a-37b9-4f0b-89b6-a5256b788a51','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767012871/bej/products/vlcyp4uqc55sqnbmrglk.webp',NULL,'57ed281c-3fd1-438a-a21d-a90f9fb54328',2),('f52106cc-73f5-457f-94ca-8125f095dd20','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765725356/bej/products/xw3qws0okcbnamqsepds.webp',NULL,'1bbd8a48-ff12-4744-b7b6-58acdeae5940',0),('f61ff980-1aff-4a30-8f95-a591e10acd87','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678002/bej/products/bvlxmaqwvnznn56dp5cf.webp',NULL,'17c27e02-97b7-4e30-9b2d-fcc19e2fef24',0),('f63fec26-6418-4d6f-b218-cea8c13d933a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678009/bej/products/vkvqmskzwxx9s6bzhhsj.webp',NULL,'6eeb68fc-19d9-49e3-86d4-72a431aa563a',1),('f669336d-6eb0-40a1-9328-d3336c809122','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112045/bej/products/msriqybz2gydzldq0sqk.webp',NULL,'57a1dea6-0216-4c60-9eb8-bbfc12647e17',1),('f6c6ce52-6494-48f0-872d-36c756b86bf9','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672097/bej/products/mwdi9flxwzvfudagadly.webp',NULL,'23ab34b0-f3b2-4580-ada0-e51511003c75',2),('f80f6c8e-59ed-409a-8855-06855727e088','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767019857/bej/products/qevssomujcmrysfffyo2.webp',NULL,'abc23749-9d07-4618-8fc7-21efbd02555b',4),('f86af159-7d4e-48ac-88f7-032ab2574ab6','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767438640/bej/products/tg6i7i8e7fkm7azzy0hy.webp',NULL,'ebd39f8c-cac2-49b5-8bec-15230d09b342',2),('f8dc46b5-0601-4cfb-b7b0-e8fd369f40b1','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678003/bej/products/ri9r1aisydtxy5qcdbzv.webp',NULL,'17c27e02-97b7-4e30-9b2d-fcc19e2fef24',1),('f9b5f693-7f15-4f7a-a6f9-735e53a2d28d','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766672086/bej/products/hoct7ngx3ty92fcdacws.webp',NULL,'82e7d0d1-6949-4280-b004-aa509de94952',2),('fa935b6e-c67d-4e51-8cab-fddd96c46844','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1766678903/bej/products/jqu7pkkqkqshrgoogjum.webp',NULL,'c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf',0),('faece2ae-4a2a-402a-997d-f6bab633f60a','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1767371829/bej/products/c5w7xxzwir8ycrnlngx3.webp',NULL,'f164ca5f-6240-473c-a175-575863b9bdd2',2),('fe4c6176-2fd0-4ed3-9195-5c8859122580','https://res.cloudinary.com/dkbfbc5s9/image/upload/v1765112049/bej/products/csrgdl4h47cegewukj46.webp',NULL,'57a1dea6-0216-4c60-9eb8-bbfc12647e17',3);
/*!40000 ALTER TABLE `product_image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_variant`
--

DROP TABLE IF EXISTS `product_variant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_variant` (
  `id` varchar(255) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `sku` varchar(255) DEFAULT NULL,
  `product_id` varchar(255) DEFAULT NULL,
  `sort_index` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgrbbs9t374m9gg43l6tq1xwdj` (`product_id`),
  CONSTRAINT `FKgrbbs9t374m9gg43l6tq1xwdj` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variant`
--

LOCK TABLES `product_variant` WRITE;
/*!40000 ALTER TABLE `product_variant` DISABLE KEYS */;
INSERT INTO `product_variant` VALUES ('06a1affa-af1e-48f6-85ca-dfd08d55e711','Công thay',NULL,'d6cec6d1-e959-450d-b646-23de4258c0bb',2),('08133f27-866f-411a-b37a-ffd6e1e8ad0a','Màu Trắng',NULL,'8f518e4e-895e-4ab3-8f4a-5bbdab3d3a2a',1),('17c27e02-97b7-4e30-9b2d-fcc19e2fef24','Màu đen',NULL,'8ef527d3-a6ff-446d-bc7f-9be965416d72',0),('1bbd8a48-ff12-4744-b7b6-58acdeae5940','Xanh Dương Nhạt',NULL,'660a1160-c655-4fb8-8cde-8dee50a3c1be',0),('1ecfc9b5-e9d7-4aca-b9af-ada38ebac9f2','Pisen',NULL,'d6cec6d1-e959-450d-b646-23de4258c0bb',1),('1febc89c-c8d7-483f-8914-fe5e5e87566c','Vàng',NULL,'87da2a8b-fc49-4cb7-8d9c-8e1c0195b29a',0),('2020408f-e503-42ca-997b-bf2474b15ca8','Vàng Nhạt',NULL,'a66b3da2-41ff-4ab3-bc36-04ef10de2e8b',2),('20d81f87-12fb-4eb1-b05e-298890d5dc07','',NULL,'4fcad8c8-ab27-4199-9844-a1f4ebce63b8',0),('23ab34b0-f3b2-4580-ada0-e51511003c75','GENA pro',NULL,'80cf6e87-4abb-460b-8c40-fd5ae78b07ee',1),('27038b18-774e-404f-96a5-b7b990849aea','Màu Xanh Dương',NULL,'4f6f678a-42ad-4608-9230-3675067835c2',2),('2ba81e79-29e2-4a4c-ab71-f12508c0c25a','',NULL,'170fe367-601f-458c-a42c-72f6dbfd2c81',0),('2f02e70e-c412-48a2-9e03-cf5525a7673e','Trắng',NULL,'09ca25bb-aa47-4562-b1dc-7635d3058b6d',0),('37fdfd6c-293d-4e76-8003-a7014ba36902','',NULL,'8f05354f-651e-484e-af65-3b8142bb3df7',0),('3ec17923-3c70-4ee5-8d8c-ca44362b4c8f','',NULL,'e34531ef-d5a9-4033-bbd8-10824e0d7cd5',0),('43efa5ab-9bf7-4e4a-9a72-c3b797e6214d','Màu trắng',NULL,'4f6f678a-42ad-4608-9230-3675067835c2',1),('50d6b686-2de3-473d-b641-f8b2ed2f1834','Bạc Ánh Trăng',NULL,'402b3525-d82d-41fb-99ce-a3f3478857e2',0),('50e7fc07-4964-440a-b28d-4483e2d1beb7','Trắng',NULL,'87da2a8b-fc49-4cb7-8d9c-8e1c0195b29a',1),('531c1c6c-2adb-4f18-ae23-6e14591226b2','',NULL,'74f870ea-e771-4a36-8a2c-41a3c0cb328d',0),('5394056d-3a8d-430c-a8ba-ac29459747ab','',NULL,'2b93e3b7-85d8-41a2-901b-bf9db01cc9fd',0),('57a1dea6-0216-4c60-9eb8-bbfc12647e17','Xanh Navy',NULL,'f262fea0-9e58-4863-85bf-c80bd28e62fa',1),('57ed281c-3fd1-438a-a21d-a90f9fb54328','Trong suốt',NULL,'1a379678-5ba7-432b-b98b-7450a268eb52',0),('597e3cbc-43ab-4b90-b9af-3ca3da861fce','',NULL,'667ae8ff-b208-47b0-badc-94c8ef288f9f',0),('63369aaf-f57f-4cf2-ad15-5f008b58b77d','',NULL,'a5345b35-ab10-49c0-8a5d-ed95c7b2b472',0),('6eeb68fc-19d9-49e3-86d4-72a431aa563a','Màu trắng',NULL,'8ef527d3-a6ff-446d-bc7f-9be965416d72',1),('706b8987-aff6-4f11-a60b-047eca77ac7c','Xanh Dương Đậm',NULL,'660a1160-c655-4fb8-8cde-8dee50a3c1be',2),('73aea84a-350c-4dbd-9440-e31380f7d802','Xanh Lá',NULL,'660a1160-c655-4fb8-8cde-8dee50a3c1be',1),('81686a0c-a74a-419e-bff1-95bc906732ee','',NULL,'70e50eea-96cb-41db-a82b-96748afa49ac',0),('8280c8a4-b6b7-40b9-ad09-f48fabbfba93','Xám Chạng Vạng',NULL,'402b3525-d82d-41fb-99ce-a3f3478857e2',1),('82e7d0d1-6949-4280-b004-aa509de94952','GENA loại A+ liền khung',NULL,'80cf6e87-4abb-460b-8c40-fd5ae78b07ee',0),('9474de3c-3158-4724-abd8-1ceed03d6ec9','Xanh Lam Khói',NULL,'37b4cf27-a191-4086-ac99-61038beb729e',0),('9f9b6aca-b8f8-413d-a4a8-5b967b06ac1f','Đỏ San Hô',NULL,'bb0762fa-840e-4de1-adb7-d191ebe7d57f',1),('a2656cca-0b2a-4296-aae0-e72b412fc4f9','Màu Tím',NULL,'96b5e3c7-65a8-47fc-9046-3907208dac62',1),('a5eb4325-c62e-4069-8b87-e5e23423108b','Đen ',NULL,'a66b3da2-41ff-4ab3-bc36-04ef10de2e8b',1),('aa16f592-74f2-4bb4-8af2-745510847031','',NULL,'7207c9f0-f3d0-4e51-b08b-5e3af6752ff6',0),('abc23749-9d07-4618-8fc7-21efbd02555b','Xanh Da Trời',NULL,'a66b3da2-41ff-4ab3-bc36-04ef10de2e8b',0),('af17f796-0768-46cd-8a2e-ac9a81ee8b82','Màu trắng',NULL,'f0d86718-f662-44b5-887a-df5f811fb027',1),('b32f254c-839e-4a32-906e-8043efe1d03e','Màu Đen',NULL,'8f518e4e-895e-4ab3-8f4a-5bbdab3d3a2a',0),('bb5d5194-344e-4f7f-9386-c6671981f22b','Tím Oải Hương',NULL,'37b4cf27-a191-4086-ac99-61038beb729e',1),('bee3f0ca-0cae-4dc3-b0fc-71943df56ce1','',NULL,'db5ecd60-639a-4ae3-9942-840254c92e79',0),('c12c774d-7b13-4137-97ac-84f9b726743d','',NULL,'f432b71f-a20a-4e48-8e8e-bf823e7052d5',0),('c1c170d1-0cf0-4e9d-be54-58db2fe70ebb','Đen',NULL,'09ca25bb-aa47-4562-b1dc-7635d3058b6d',1),('c23e1cdd-4adf-4fb3-afd6-d3aad91a0faa','Màu Đen',NULL,'96b5e3c7-65a8-47fc-9046-3907208dac62',0),('c3d2a1c4-4167-4b6c-ae5d-b285f23eefcf','Đen Bóng Đêm',NULL,'402b3525-d82d-41fb-99ce-a3f3478857e2',2),('c6084988-a857-437b-ba6e-913518022224','Màu đen',NULL,'f0d86718-f662-44b5-887a-df5f811fb027',0),('c9e48711-ed3c-484d-8158-2cef705fb45e','GENA',NULL,'d6cec6d1-e959-450d-b646-23de4258c0bb',0),('ccb1f63d-8fcb-4ca3-bef7-56669ee30670','Xám bạc',NULL,'f262fea0-9e58-4863-85bf-c80bd28e62fa',0),('d1c4661e-e01e-44ac-9d32-a2199d1df2d3','Công thay ',NULL,'21eaa059-67e4-4651-bc2f-0556413ec539',1),('e7df39fd-0db5-400b-868d-92bbaa275df3','Đen Tuyền',NULL,'bb0762fa-840e-4de1-adb7-d191ebe7d57f',0),('eac5da1f-40ef-416c-93b5-458fb04f7773','',NULL,'f7482c36-6446-45ea-bcb0-332676eb3729',0),('ebd39f8c-cac2-49b5-8bec-15230d09b342','Trắng',NULL,'431fdadb-f3e1-4a62-ade1-9a9aea8e5e88',0),('f164ca5f-6240-473c-a175-575863b9bdd2','',NULL,'21eaa059-67e4-4651-bc2f-0556413ec539',0),('f4286275-3337-41fe-af41-f30cc4abf311','Màu đen',NULL,'4f6f678a-42ad-4608-9230-3675067835c2',0),('f4646957-1ba8-4b63-93ca-6ccdd4c32659','Xám Bạc',NULL,'660a1160-c655-4fb8-8cde-8dee50a3c1be',3);
/*!40000 ALTER TABLE `product_variant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `name` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('ADMIN','system admin'),('EMPLOYEE_MANAGER','hr'),('SHOP_MANAGER','hr'),('USER','user');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_name` varchar(255) NOT NULL,
  `permissions_name` varchar(255) NOT NULL,
  PRIMARY KEY (`role_name`,`permissions_name`),
  KEY `FKf5aljih4mxtdgalvr7xvngfn1` (`permissions_name`),
  CONSTRAINT `FKcppvu8fk24eqqn6q4hws7ajux` FOREIGN KEY (`role_name`) REFERENCES `role` (`name`),
  CONSTRAINT `FKf5aljih4mxtdgalvr7xvngfn1` FOREIGN KEY (`permissions_name`) REFERENCES `permission` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES ('ADMIN','MANAGE_PRODUCT'),('SHOP_MANAGER','MANAGE_PRODUCT'),('ADMIN','MANAGE_ROLE'),('ADMIN','MANAGE_STAFF'),('EMPLOYEE_MANAGER','MANAGE_STAFF');
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shift`
--

DROP TABLE IF EXISTS `shift`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shift` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` time(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `start_time` time(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shift`
--

LOCK TABLES `shift` WRITE;
/*!40000 ALTER TABLE `shift` DISABLE KEYS */;
INSERT INTO `shift` VALUES (1,'22:00:00.000000','ca tối','16:00:00.000000'),(2,'13:00:00.000000','ca chiều','17:00:00.000000'),(3,'12:00:00.000000','ca sáng','08:00:00.000000');
/*!40000 ALTER TABLE `shift` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supply`
--

DROP TABLE IF EXISTS `supply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supply` (
  `id` varchar(255) NOT NULL,
  `price` double NOT NULL,
  `quantity` int NOT NULL,
  `type` int NOT NULL,
  `attribute_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKfxksjbbr3yohhqkijo04w6l7x` (`attribute_id`),
  CONSTRAINT `FKfxksjbbr3yohhqkijo04w6l7x` FOREIGN KEY (`attribute_id`) REFERENCES `product_attribute` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supply`
--

LOCK TABLES `supply` WRITE;
/*!40000 ALTER TABLE `supply` DISABLE KEYS */;
/*!40000 ALTER TABLE `supply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `task`
--

DROP TABLE IF EXISTS `task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `task` (
  `id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `schedule_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4qtkj48h77kpufjk5lxqh1hrr` (`schedule_id`),
  CONSTRAINT `FK4qtkj48h77kpufjk5lxqh1hrr` FOREIGN KEY (`schedule_id`) REFERENCES `work_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `task`
--

LOCK TABLES `task` WRITE;
/*!40000 ALTER TABLE `task` DISABLE KEYS */;
/*!40000 ALTER TABLE `task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `dob` date DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('33ff2646-2922-416c-8216-c877201ed659','admin','2025-12-03','admin@gmail.com','admin','$2a$10$Dkxp5l6Bs0z9cTDFdRDz5u7th3l8MsM3U6Qm/OfRpuB1z35CrawRe','admin'),('6895cccc-4891-4c15-981a-e59a2d16a939','','2003-02-01','culacgi0ntan27@gmail.com','Nguyễn Trọng Đức','$2a$10$K6kdNc7TE24egJ9.pnnSqO2vBddj5BgUDNkp./fu.r4H1xoXQGYoK','044'),('7776ba1d-f65d-470e-bf77-d0715a7907a4','',NULL,'dttd@gmail.com','Dũng Tiến','$2a$10$6WspeWeQUAbRnOVAoafGGuncDl.08Z.Cj8mo/.zaHzflVPX3d3AV.','0111222333'),('7cacd02a-7e5b-4321-ba46-b3500ccf8589','',NULL,'abcdzyx027@gmail.com','Đặng Tiến Dũng','$2a$10$pGAEaNAdoORJD1c.kPxnRegR2ce6CB.rRXGlNBYO1uxJuZYc7gcRC','0986068436'),('8460cfc0-ca09-4ce2-98bc-0fe5f6ba015b','',NULL,'adu113@gmail.com','Nguyễn Thị V','$2a$10$wGmzfiyfIjKeQHhmNPseguTnpWt59VyH4nlEr6GJt/35zUPBRNPRe','0333333333'),('8d04670e-170d-40fe-9477-545169f62832',NULL,NULL,'adu112@gmail.com',NULL,'$2a$10$ADiI.Gd7rgcTno9fUNzGp.AuSZm74atoVQ3ikg2r0R.xC60AnOMs2','0222222222'),('a1b2c3d4-e5f6-7890-abcd-ef1234567890','Hà Nội','1995-05-15','nguyenvana@gmail.com','Nguyễn Văn A','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','0912345678'),('b2c3d4e5-f6a7-8901-bcde-f12345678901','TP.HCM','1998-08-20','tranthib@gmail.com','Trần Thị B','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','0923456789'),('b78ae41e-2ec3-4065-a876-06a16a039f15','Hn','2003-07-21','adu111','Đặng Tiến Dũng','$2a$10$5qXSUvNSFkqO50707YPGdul.JlbAzXnwTgHfexPagSx5UHAPxEvhC','0111111111'),('c3d4e5f6-a7b8-9012-cdef-123456789012','Đà Nẵng','1990-12-10','levanc@gmail.com','Lê Văn C','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','0934567890'),('d4e5f6a7-b8c9-0123-def0-234567890123','Hải Phòng','1992-03-25','phamthid@gmail.com','Phạm Thị D','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','0945678901'),('e5f6a7b8-c9d0-1234-ef01-345678901234','Cần Thơ','1988-11-30','hoangvane@gmail.com','Hoàng Văn E','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','0956789012'),('ff5e7dc7-0c39-4b37-aba3-da4b267c88cf','An Duong, Yen Phu, Tay Ho, Ha Noi','2003-01-01','test1@gmail.com','Tiến Dũng','$2a$10$XkuGB2D8hJi3cn96sqhd0en9u0bipz7e9gvWtfJ2QQovQ.5SaFaZG','0123123123');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` varchar(255) NOT NULL,
  `roles_name` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`,`roles_name`),
  KEY `FK6pmbiap985ue1c0qjic44pxlc` (`roles_name`),
  CONSTRAINT `FK55itppkw3i07do3h7qoclqd4k` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK6pmbiap985ue1c0qjic44pxlc` FOREIGN KEY (`roles_name`) REFERENCES `role` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES ('33ff2646-2922-416c-8216-c877201ed659','ADMIN'),('7776ba1d-f65d-470e-bf77-d0715a7907a4','ADMIN'),('7cacd02a-7e5b-4321-ba46-b3500ccf8589','ADMIN'),('b78ae41e-2ec3-4065-a876-06a16a039f15','SHOP_MANAGER'),('e5f6a7b8-c9d0-1234-ef01-345678901234','SHOP_MANAGER'),('33ff2646-2922-416c-8216-c877201ed659','USER'),('6895cccc-4891-4c15-981a-e59a2d16a939','USER'),('8460cfc0-ca09-4ce2-98bc-0fe5f6ba015b','USER'),('8d04670e-170d-40fe-9477-545169f62832','USER'),('a1b2c3d4-e5f6-7890-abcd-ef1234567890','USER'),('b2c3d4e5-f6a7-8901-bcde-f12345678901','USER'),('c3d4e5f6-a7b8-9012-cdef-123456789012','USER'),('d4e5f6a7-b8c9-0123-def0-234567890123','USER'),('ff5e7dc7-0c39-4b37-aba3-da4b267c88cf','USER');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_schedule`
--

DROP TABLE IF EXISTS `work_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_schedule` (
  `id` varchar(255) NOT NULL,
  `work_date` date DEFAULT NULL,
  `shift_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKecv0f0fbx9xaugnudb23j73yf` (`shift_id`),
  CONSTRAINT `FKecv0f0fbx9xaugnudb23j73yf` FOREIGN KEY (`shift_id`) REFERENCES `shift` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_schedule`
--

LOCK TABLES `work_schedule` WRITE;
/*!40000 ALTER TABLE `work_schedule` DISABLE KEYS */;
INSERT INTO `work_schedule` VALUES ('21f9f857-b1e1-4f01-b17a-2461e119ddb4','2025-11-12',1),('80a11c40-d34c-4b11-aee1-c008580f06a6','2025-11-08',1),('8233fff7-19ee-4495-993c-98c87a52d5f9','2025-11-13',1),('dab0a215-7455-4b8f-b777-8992202799a9','2025-11-19',1),('w1a2b3c4-d5e6-7890-abcd-ef1234567890','2025-11-20',2),('w2b3c4d5-e6f7-8901-bcde-f12345678901','2025-11-21',3),('w3c4d5e6-f7a8-9012-cdef-123456789012','2025-11-22',1),('w4d5e6f7-a8b9-0123-def0-234567890123','2025-11-23',2),('w5e6f7a8-b9c0-1234-ef01-345678901234','2025-11-24',3);
/*!40000 ALTER TABLE `work_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `work_schedule_users`
--

DROP TABLE IF EXISTS `work_schedule_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `work_schedule_users` (
  `work_schedule_id` varchar(255) NOT NULL,
  `users_id` varchar(255) NOT NULL,
  PRIMARY KEY (`work_schedule_id`,`users_id`),
  KEY `FKlclio9mgtepvvgj2iwmu29ps6` (`users_id`),
  CONSTRAINT `FKlclio9mgtepvvgj2iwmu29ps6` FOREIGN KEY (`users_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKoorh76k38she07yiv2ublql86` FOREIGN KEY (`work_schedule_id`) REFERENCES `work_schedule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `work_schedule_users`
--

LOCK TABLES `work_schedule_users` WRITE;
/*!40000 ALTER TABLE `work_schedule_users` DISABLE KEYS */;
INSERT INTO `work_schedule_users` VALUES ('21f9f857-b1e1-4f01-b17a-2461e119ddb4','24daab77-a1cb-4e14-b002-cb0754acbc52'),('80a11c40-d34c-4b11-aee1-c008580f06a6','24daab77-a1cb-4e14-b002-cb0754acbc52'),('8233fff7-19ee-4495-993c-98c87a52d5f9','24daab77-a1cb-4e14-b002-cb0754acbc52'),('dab0a215-7455-4b8f-b777-8992202799a9','24daab77-a1cb-4e14-b002-cb0754acbc52'),('w2b3c4d5-e6f7-8901-bcde-f12345678901','24daab77-a1cb-4e14-b002-cb0754acbc52'),('21f9f857-b1e1-4f01-b17a-2461e119ddb4','b78ae41e-2ec3-4065-a876-06a16a039f15'),('80a11c40-d34c-4b11-aee1-c008580f06a6','b78ae41e-2ec3-4065-a876-06a16a039f15'),('8233fff7-19ee-4495-993c-98c87a52d5f9','b78ae41e-2ec3-4065-a876-06a16a039f15'),('dab0a215-7455-4b8f-b777-8992202799a9','b78ae41e-2ec3-4065-a876-06a16a039f15'),('w3c4d5e6-f7a8-9012-cdef-123456789012','b78ae41e-2ec3-4065-a876-06a16a039f15'),('w1a2b3c4-d5e6-7890-abcd-ef1234567890','c3d4e5f6-a7b8-9012-cdef-123456789012'),('w4d5e6f7-a8b9-0123-def0-234567890123','c3d4e5f6-a7b8-9012-cdef-123456789012'),('w1a2b3c4-d5e6-7890-abcd-ef1234567890','e5f6a7b8-c9d0-1234-ef01-345678901234'),('w5e6f7a8-b9c0-1234-ef01-345678901234','e5f6a7b8-c9d0-1234-ef01-345678901234');
/*!40000 ALTER TABLE `work_schedule_users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-06  0:42:40
