# Reviste IMG Extractor

Script ajutător pentru a genera conţinutul necesar site-ului [Arhiva Reviste Vechi](http://arhivarevistevechi.mythweb.ro) ([repository aici](https://github.com/cristan2/ArhivaRevisteVechi)).
 
Aplicaţia citeşte fişiere PDF dintr-un director, extrage fiecare pagină ca o imagine JPG separată, pe care o salvează într-un director-destinaţie dat, creând şi structura de directoare necesară pentru a putea fi folosită direct de către site.

La baza aplicaţiei stă librăria [Apache PDFBox](https://pdfbox.apache.org/), care citeşte fişierele PDF şi scrie imaginile JPG.

Toate setările trebuie făcute în `RevConfig.java`.


## TODO
* Mută toate setările din `PdfToImgExtractor` în `RevConfig.java`
* Mută apoi toate setările în fişiere de configurare
* Implement logging şi log levels
* Mută proiect în Maven
* END GOAL: jar standalone + fişier configurare
