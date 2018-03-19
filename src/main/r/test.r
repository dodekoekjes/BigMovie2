library(RMySQL) # Include Rserve Library
library(base64enc) # Include base64enc Library
library(png) # Include png Library

#Pie Chart Function
myChart = function(a, b){
    slices <- c(a, b)  # Create slices with the values of parameters
    lbls <- c("Actor", "Actress") # Labels
    pct <- round(slices/sum(slices)*100) # Formula for the slices
    lbls <- paste(lbls, pct) # add percents to labels 
    lbls <- paste(lbls,"%",sep="") # ad % to labels 

    png(file="./src/main/r/test.png",width=480,height=480) # Save png
    pie(slices,labels = lbls, col=rainbow(length(lbls)),main="Differences between Actors and Actresses") # Create pie chart

    dev.off()

    img <- readPNG("./src/main/r/test.png",TRUE) # Read PNG 
    img64 <- dataURI(writePNG(img,raw()), "image/png") # Encode to URI
    return("IMAGE") 
}

#Query function
meaningToQ = function(q,r) {
    mydb <- dbConnect(MySQL(), user="u334588916_baap", password="wz;J^MO66uVn|IV3hd", dbname="u334588916_bmov", host="sql40.main-hosting.eu") # DB connection

    query = dbSendQuery(mydb, q) #Send Query
    if(r==FALSE){
        data = fetch(query, n=-1) #Get data for non-R question
    }else{
        result = fetch(query,n=-1) #Get data for R question
        x = result[1,1]
        y = result[2,1]
        data = myChart(x,y)
    }
    

    # dbClearResult(data)
    dbDisconnect(mydb)
    return(data)
}
#meaningToQ("SELECT id FROM `title_type`",TRUE)

