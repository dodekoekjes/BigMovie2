library(RMySQL)
library(base64enc)
library(png)

myChart = function(a, b){
    slices <- c(a, b) 
    lbls <- c("Actor", "Actress")
    pct <- round(slices/sum(slices)*100)
    lbls <- paste(lbls, pct) # add percents to labels 
    lbls <- paste(lbls,"%",sep="") # ad % to labels 

    png(file="./src/main/r/test.png",width=480,height=480)
    pie(slices,labels = lbls, col=rainbow(length(lbls)),main="Differences between Actors and Actresses") 

    dev.off()

    img <- readPNG("./src/main/r/test.png",TRUE)
    img64 <- dataURI(writePNG(img,raw()), "image/png")
    return("IMAGE")
}

meaningToQ = function(q,r) {
    mydb <- dbConnect(MySQL(), user="u334588916_baap", password="wz;J^MO66uVn|IV3hd", dbname="u334588916_bmov", host="sql40.main-hosting.eu")

    query = dbSendQuery(mydb, q)
    if(r==FALSE){
        data = fetch(query, n=-1)
    }else{
        result = fetch(query,n=-1)
        x = result[1,1]
        y = result[2,1]
        data = myChart(x,y)
    }
    

    # dbClearResult(data)
    dbDisconnect(mydb)
    return(data)
}
#meaningToQ("SELECT id FROM `title_type`",TRUE)

