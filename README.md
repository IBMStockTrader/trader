The trader service provides the UI for the Portfolio sample.  The main entry point is the "summary" servlet, which lets you choose an operation and a portfolio to act upon.  It transfers control to other servlets, such as "addPortfolio", "viewPortfolio", and "addStock", each of which transfer control back to "summary" when done.  The "viewPortfolio" and "addStock" servlets expect a query param named "owner".

Each page has a header and footer image, and there's an index.html that redirects to the "summary" servlet.

The servlets just concern themselves with constructing the right HTML to return.  The UI is very basic; there's no use of JavaScript or anything fancy.  All of the real logic is in the PortfolioServices.java, which contains all of the REST calls to the Portfolio microservice, and appropriate JSON wrangling.

You can hit the main entry point by entering a URL such as http://localhost:9080/trader/summary in your browser's address bar.
