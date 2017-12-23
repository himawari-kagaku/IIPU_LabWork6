package sample;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class Controller {

    // Properties
    TreeView<Label> treeView;
    AnchorPane treePane = new AnchorPane();
    Button connectButton = new Button("Connect");
    Hyperlink refreshLink = new Hyperlink("↺");
    Boolean isUpdateWorking = true;
    volatile Boolean isConnecting = false;

    public AnchorPane getRoot(){
        return treePane;
    }

    public TreeView<Label> getTreeView() {
        ArrayList<NavigableMap<Integer, LinkedList<String>>> tree = null;
        try {
            tree = parseLines();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TreeItem<Label> rootItem = new TreeItem<Label> (new Label("Wi-Fi"));
        rootItem.setExpanded(true);
        for (NavigableMap<Integer, LinkedList<String>> map : tree) {
            int key = Collections.max(map.keySet());
            int prevKey = map.lowerKey(key);
            if(prevKey>=12) {
                TreeItem<Label> item = new TreeItem<Label> (new Label(map.get(prevKey).get(0).trim()));
                for (String line : map.get(key)) {
                    for (int i = 0; i < key; i++) System.out.print("");
                    TreeItem<Label> subitem = new TreeItem<Label>(new Label(line.trim()));
                    item.getChildren().add(subitem);
                }
                rootItem.getChildren().add(item);
            }
        }
        treeView = new TreeView<>(rootItem);
        return treeView;
    }

    public Button getConnectButton(){
        connectButton.setOnAction(e -> connectButtonAction());
        return connectButton;
    }

    public Hyperlink getRefreshLink(){
        refreshLink.setOnAction(e -> refreshLinkAction());
        return refreshLink;
    }

    public void start(){
        try {

            Thread myThready = new Thread(new Runnable()
            {
                int lastSize=0;

                public void run()
                {
                    while(isUpdateWorking){
                        if(!isConnecting){
                            try {
                                if(lastSize!=getLines().size()){
                                    lastSize=getLines().size();
                                    treeView = getTreeView();
                                    treeView.refresh();
                                    Platform.runLater(new Runnable(){
                                        @Override
                                        public void run() {
                                            treePane.getChildren().clear();
                                            treePane.getChildren().add(treeView);
                                            treePane.getChildren().add(connectButton);
                                            treePane.getChildren().add(refreshLink);
                                            AnchorPane.setTopAnchor(treeView, 0.0);
                                            AnchorPane.setRightAnchor(treeView, 0.0);
                                            AnchorPane.setLeftAnchor(treeView, 0.0);
                                            AnchorPane.setBottomAnchor(treeView, 50.0);
                                            AnchorPane.setRightAnchor(connectButton, 0.0);
                                            AnchorPane.setLeftAnchor(connectButton, 0.0);
                                            AnchorPane.setBottomAnchor(connectButton, 0.0);
                                            AnchorPane.setRightAnchor(refreshLink, 20.0);
                                            AnchorPane.setTopAnchor(refreshLink, 0.0);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            myThready.start();
        } catch(Exception ex){
            ex.printStackTrace();
        }

    }

    public void stop() {
        isUpdateWorking = false;
    }

    private static void printNote(NodeList nodeList) {

        for (int count = 0; count < nodeList.getLength(); count++) {

            Node tempNode = nodeList.item(count);

            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());

                if (tempNode.hasAttributes()) {

                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();

                    for (int i = 0; i < nodeMap.getLength(); i++) {

                        Node node = nodeMap.item(i);
                        System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());

                    }

                }

                if (tempNode.hasChildNodes()) {

                    // loop again if has child nodes
                    printNote(tempNode.getChildNodes());

                }

                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");

            }

        }

    }

    // Private methods
    private ArrayList<String> getLines() throws Exception {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader mountOutput = null;
        try {
            Process mountProcess = Runtime.getRuntime().exec("system_profiler SPAirPortDataType");

            mountOutput = new BufferedReader(new InputStreamReader(
                    mountProcess.getInputStream()));
            while (true) {
                String line = mountOutput.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (mountOutput != null) {
                mountOutput.close();
            }
        }
        return lines;
    }

    private ArrayList<NavigableMap<Integer, LinkedList<String>>> parseLines() throws Exception {
        ArrayList<String> lines = getLines();
        ArrayList<NavigableMap<Integer, LinkedList<String>>> tree = new ArrayList<>();
        int prevK = 0, node = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isEmpty()) continue;
            int k = 0;
            while (line.charAt(k) == ' ') {
                k++;
            }

            if (k >= prevK) {
                prevK = k;
            } else {
                node++;
                prevK = k;
            }

            if (node == tree.size()) {
                tree.add(new TreeMap<>());
            }
            if (!tree.get(node).containsKey(k)) {
                tree.get(node).put(k, new LinkedList<>());
            }
            tree.get(node).get(k).add(line);
            System.out.println("Added at "+node+" by key "+k+" "+line);

        }

        for (NavigableMap<Integer, LinkedList<String>> map : tree) {
            int key = Collections.max(map.keySet());
            int prevKey = map.lowerKey(key);
            //System.out.print(prevKey+" ");
            //System.out.println(map.get(prevKey).get(0));
            for (String line : map.get(key)) {
                //System.out.print(key+" ");
                //for (int i = 0; i < key; i++) System.out.print("");
                //System.out.println(line);
            }
        }
        return tree;
    }

    private void connectButtonAction(){

        isConnecting=true;
        Label nameLabel = (Label)treeView.getSelectionModel().getSelectedItem().getValue();
        String networkName = nameLabel.getText().substring(0, nameLabel.getText().length()-1);

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Connect to "+networkName);
        dialog.setContentText("Please, enter password:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Password: " + result.get());
            try {
                //String command = "sudo networksetup -setairportnetwork en0 '"+networkName+"' "+result.get();
                String[] cmd = {"networksetup", "-setairportnetwork", "en0", networkName, result.get()};
                System.out.println(cmd);
                Process mountProcess = Runtime.getRuntime().exec(cmd);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isConnecting=false;

    }

    private void refreshLinkAction(){
        treeView = getTreeView();
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                treePane.getChildren().clear();
                treePane.getChildren().add(treeView);
                treePane.getChildren().add(connectButton);
                treePane.getChildren().add(refreshLink);
                AnchorPane.setTopAnchor(treeView, 0.0);
                AnchorPane.setRightAnchor(treeView, 0.0);
                AnchorPane.setLeftAnchor(treeView, 0.0);
                AnchorPane.setBottomAnchor(treeView, 50.0);
                AnchorPane.setRightAnchor(connectButton, 0.0);
                AnchorPane.setLeftAnchor(connectButton, 0.0);
                AnchorPane.setBottomAnchor(connectButton, 0.0);
                AnchorPane.setRightAnchor(refreshLink, 20.0);
                AnchorPane.setTopAnchor(refreshLink, 0.0);
            }
        });
    }
}
