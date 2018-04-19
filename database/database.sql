create table IF NOT EXISTS `title` (
   `id` int unsigned not null auto_increment,
   `primaryTitle` varchar(255) not null,
   `originalTitle` varchar(255) not null,
   `isAdult` bit(1) not null,
   `startYear` int unsigned,
   `endYear` int unsigned,
   `runtimeMinutes` int unsigned,
		
   primary key (`id`)
);

create table IF NOT EXISTS `title_type`(
	`title_id` int unsigned not null,
	`name` varchar(50) not null,
	
	 foreign key (`title_id`) references `title`(`id`)
		on update cascade
		on delete cascade
);

create table IF NOT EXISTS `name` (
   `id` int unsigned not null auto_increment,
   `primaryName` varchar(255) not null,
   `birthYear` int unsigned not null,
   `deathYear` int unsigned,

   primary key (`id`)
);

create table IF NOT EXISTS `name_profession` (
   `name_id` int unsigned not null,
   `profession` varchar(255) not null,

   foreign key (`name_id`) references `name`(`id`)
      on update cascade
      on delete cascade
);

create table IF NOT EXISTS `name_known_for_titles` (
   `name_id` int unsigned not null,
   `title_id` int unsigned not null,

   foreign key (`name_id`) references `name`(`id`)
      on update cascade
      on delete cascade,

   foreign key (`title_id`) references `title`(`id`)
      on update cascade
      on delete cascade
);

create table IF NOT EXISTS `directors` (
	`title_id` int unsigned not null,
	`name_id` int unsigned not null,
	
	foreign key (`title_id`) references `title`(`id`)
		on update cascade
		on delete cascade,
		
	foreign key (`name_id`) references `name`(id)
		on update cascade
		on delete cascade
		
);

create table IF NOT EXISTS `writers` (
	`title_id` int unsigned not null,
	`name_id` int unsigned not null,
	
	foreign key (`title_id`) references `title`(`id`)
		on update cascade
		on delete cascade,
		
	foreign key (`name_id`) references `name`(id)
		on update cascade
		on delete cascade
		
);

create table IF NOT EXISTS `episode` (
   `id` int unsigned not null auto_increment,
   `parentTconst` int unsigned not null,
   `seasonNumber` int unsigned,
   `episodeNumber` int unsigned,

   foreign key (`parentTconst`) references `title`(`id`)
      on update cascade
      on delete cascade,

   primary key (`id`)
);

create table IF NOT EXISTS `genre` (
	`title_id` int unsigned not null,
	`name` varchar(255) not null,
	
	foreign key (`title_id`) references `title`(`id`)
		on update cascade
		on delete cascade
);

create table IF NOT EXISTS `principal` (
   `title_id` int unsigned not null,
   `name_id` int unsigned not null,
   
   foreign key (`title_id`) references `title`(`id`)
		on update cascade
		on delete cascade,
		
	foreign key (`name_id`) references `name`(`id`)
		on update cascade
		on delete cascade
);

create table IF NOT EXISTS `rating` (
   `title_id` int unsigned not null,
   `averageRating` float(2,2),
   `numVotes` int unsigned,

   foreign key (`title_id`) references `title`(`id`)
      on update cascade
      on delete cascade
);