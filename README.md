# ParisJUG event-publisher project

a CLI used by the ParisJUG crew to publish events
[![Contribute](https://www.eclipse.org/che/factory-contribute.svg)](https://che.openshift.io/f?url=https://github.com/parisjug/event-publisher)

Currently supported:
```bash
parisjug-event-publisher gcal https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208 # will generate a google calendar url, fetching data from the wiki page in parameter
parisjug-event-publisher campaign https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208 # create a sendinblue campaign, fetching data from the wiki page in parameter
```