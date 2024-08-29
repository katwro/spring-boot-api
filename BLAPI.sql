USE master
GO
CREATE DATABASE [BLAPI]
GO

USE [BLAPI]
GO

/****** Object:  Table [dbo].[Author]    Script Date: 14.08.2024 17:30:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Author](
	[AuthorID] [int] IDENTITY(1,1) NOT NULL,
	[FirstName] [nvarchar](25) NOT NULL,
	[LastName] [nvarchar](50) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[AuthorID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Book]    Script Date: 14.08.2024 17:30:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Book](
	[BookID] [int] IDENTITY(1,1) NOT NULL,
	[Title] [nvarchar](100) NOT NULL,
	[ISBN] [nvarchar](17) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[BookID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[Book_Author]    Script Date: 14.08.2024 17:30:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Book_Author](
	[BookID] [int] NOT NULL,
	[AuthorID] [int] NOT NULL,
 CONSTRAINT [PK_BookID_AuthorID] PRIMARY KEY CLUSTERED 
(
	[BookID] ASC,
	[AuthorID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[Book_Author]  WITH CHECK ADD  CONSTRAINT [FK_Author_AuthorID] FOREIGN KEY([AuthorID])
REFERENCES [dbo].[Author] ([AuthorID])
GO
ALTER TABLE [dbo].[Book_Author] CHECK CONSTRAINT [FK_Author_AuthorID]
GO
ALTER TABLE [dbo].[Book_Author]  WITH CHECK ADD  CONSTRAINT [FK_Book_BookID] FOREIGN KEY([BookID])
REFERENCES [dbo].[Book] ([BookID])
GO
ALTER TABLE [dbo].[Book_Author] CHECK CONSTRAINT [FK_Book_BookID]
GO

INSERT INTO Author (FirstName,LastName) VALUES ('Mark','Haddon')
GO
INSERT INTO Author (FirstName,LastName) VALUES ('Viktor','Farcic')
GO
INSERT INTO Author (FirstName,LastName) VALUES ('Alex','Garcia')
GO
INSERT INTO Author (FirstName,LastName) VALUES ('Malcolm','McDonald')
GO

INSERT INTO Book (Title,ISBN) VALUES ('The Curious Incident of the Dog in the Night-Time','978-0-099-45025-2')
GO
INSERT INTO Book (Title,ISBN) VALUES ('Test-Driven Java Development','978-1-78398-742-9')
GO
INSERT INTO Book (Title,ISBN) VALUES ('Web Security For Developers','978-1-59327-994-3')
GO
INSERT INTO Book (Title,ISBN) VALUES ('A Spot of Bother','978-0-307-38769-1')
GO

INSERT INTO Book_Author (BookID,AuthorID) VALUES (1,1)
GO
INSERT INTO Book_Author (BookID,AuthorID) VALUES (2,2)
GO
INSERT INTO Book_Author (BookID,AuthorID) VALUES (2,3)
GO
INSERT INTO Book_Author (BookID,AuthorID) VALUES (3,4)
GO
INSERT INTO Book_Author (BookID,AuthorID) VALUES (4,1)
GO