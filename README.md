# ParisJUG event-publisher project

Downloads: https://github.com/parisjug/event-publisher/releases

a CLI used by the ParisJUG crew to publish events

Open the project in Eclipse Che developer environment (hosted by Red Hat, requires an account to be created first https://developers.redhat.com/developer-sandbox/ide): [![Contribute](https://www.eclipse.org/che/factory-contribute.svg)](https://workspaces..openshift.com#https://github.com/parisjug/event-publisher)

Currently supported:
```bash
parisjug-event-publisher gcal https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208 # will generate a google calendar url, fetching data from the wiki page in parameter
parisjug-event-publisher campaign https://www.parisjug.org/xwiki/wiki/oldversion/view/Meeting/20201208 # create a sendinblue campaign, fetching data from the wiki page in parameter
```

To have it working, the CLI needs the sendinblue api-key. You can generate new api-key from https://account.sendinblue.com/advanced/api.
Then, use it either with env variable:
```
export SENDINBLUE_APIKEY=<your-apikey>
```
or set to the `src/main/resources/application.properties`:
```
sendinblue.apikey=<your-apikey>
```
or in Eclipse Che/CodeReady workspaces

```sendinblue.yaml
apiVersion: v1
kind: Secret
metadata:
  name: sendinblue-secret
  labels:
    app.kubernetes.io/part-of: che.eclipse.org
    app.kubernetes.io/component: workspace-secret
  annotations:
    che.eclipse.org/automount-workspace-secret: 'true'
    che.eclipse.org/env-name: SENDINBLUE_APIKEY
    che.eclipse.org/mount-as: env
data:
  mykey: <you-api-key-encoded-in-base64>
```

```
kubectl apply -f sendinblue.yaml -n <namespace-were-your-workspace-is-running>
```
