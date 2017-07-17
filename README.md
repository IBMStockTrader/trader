<!--
       Copyright 2017 IBM Corp All Rights Reserved

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

The *trader* service provides the UI for the *Portfolio* sample.  The main entry point is the **summary**
servlet, which lets you choose an operation and a portfolio to act upon.  It transfers control to other
servlets, such as **addPortfolio**, **viewPortfolio**, and **addStock**, each of which transfers control back
to **summary** when done.  The **viewPortfolio** and **addStock** servlets expect a query param named *owner*.

Each page has a header and footer image, and there's an index.html that redirects to the **summary** servlet.

The servlets just concern themselves with constructing the right **HTML** to return.  The UI is very basic; there
is no use of **JavaScript** or anything fancy.  All of the real logic is in the PortfolioServices.java, which
contains all of the REST calls to the Portfolio microservice, and appropriate JSON wrangling.

You can hit the main entry point by entering a URL such as `http://localhost:9080/trader/summary` in your
browser's address bar.
