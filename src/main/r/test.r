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

meaningToQ = function(q) {
    mydb <- dbConnect(MySQL(), user='root', password='', dbname='imdb', host='127.0.0.1', port=3306)

    query = dbSendQuery(mydb, q)
    # query = dbSendQuery(mydb, statement = paste(
    #     "SELECT *",
    #     "FROM name",
    #     "WHERE id < 5"
    # ))



    data = fetch(query, n=-1)
    # if(r==FALSE){
    #     data = fetch(query, n=-1)
    # }else{
    #     result = fetch(query,n=-1)
    #     x = result[1,1]
    #     y = result[2,1]
    #     data = myChart(x,y)
    # }
    

    # dbClearResult(data)
    dbDisconnect(mydb)
    return(toString(data))
}
#meaningToQ("SELECT id FROM `title_type`",TRUE)

testF = function() {
    mydb <- dbConnect(MySQL(), user='root', password='', dbname='imdb', host='127.0.0.1', port=3306)

    # query = dbSendQuery(mydb, q)
    query = dbSendQuery(mydb, statement = paste(
        "SELECT *",
        "FROM name",
        "WHERE id < 5"
    ))

    data = fetch(query, n=-1)

    return(toString(data))
}