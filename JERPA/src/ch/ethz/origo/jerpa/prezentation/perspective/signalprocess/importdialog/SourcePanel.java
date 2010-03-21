package ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.importdialog;

/**
 * Panel pro v�b�r projekt� s pr�m�ry k importov�n�.
 * 
 * 
 * @author Jiri Kucera (jERP Studio)
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.1.0 (3/20/2010)
 * @since 0.1.0 (3/20/2010)
 * 
 */
public class SourcePanel extends javax.swing.JPanel {

	/** Only for serialization */
	private static final long serialVersionUID = 8374978802927604490L;
	
	private ImportDialogProvider importDialogProvider;

	/**
	 * Creates new form SourcePanel
	 * 
	 * @param importDialogProvider
	 */
	public SourcePanel(ImportDialogProvider importDialogProvider) {
		this.importDialogProvider = importDialogProvider;
		initComponents();
		buttonGroup.add(filesButton);
		buttonGroup.add(projectsButton);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		buttonGroup = new javax.swing.ButtonGroup();
		jLabel1 = new javax.swing.JLabel();
		projectsButton = new javax.swing.JRadioButton();
		filesButton = new javax.swing.JRadioButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		projectsList = new javax.swing.JList();
		addFileButton = new javax.swing.JButton();
		removeButton = new javax.swing.JButton();
		jLabel2 = new javax.swing.JLabel();

		jLabel1.setText("Select source to import averages from:");

		projectsButton.setText("Opened projects");
		projectsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				projectsButtonActionPerformed(evt);
			}
		});

		filesButton.setSelected(true);
		filesButton.setText("Project files");
		filesButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				filesButtonActionPerformed(evt);
			}
		});

		projectsList.setModel(new javax.swing.AbstractListModel() {
			private static final long serialVersionUID = 4861755758958531703L;
			
			String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

			public int getSize() {
				return strings.length;
			}

			public Object getElementAt(int i) {
				return strings[i];
			}
		});
		jScrollPane1.setViewportView(projectsList);

		addFileButton.setText("Add file...");
		addFileButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addFileButtonActionPerformed(evt);
			}
		});

		removeButton.setText("Remove item");
		removeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				removeButtonActionPerformed(evt);
			}
		});

		jLabel2.setText("Projects for import:");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addGroup(
						layout.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING).addComponent(
								jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402,
								Short.MAX_VALUE).addComponent(jLabel1).addGroup(
								layout.createSequentialGroup().addComponent(projectsButton)
										.addGap(18, 18, 18).addComponent(filesButton)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(addFileButton).addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(removeButton).addGap(6, 6, 6)).addComponent(
								jLabel2)).addContainerGap()));
		layout
				.setVerticalGroup(layout.createParallelGroup(
						javax.swing.GroupLayout.Alignment.LEADING).addGroup(
						layout.createSequentialGroup().addContainerGap().addComponent(
								jLabel1).addPreferredGap(
								javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(
								layout.createParallelGroup(
										javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
										projectsButton).addComponent(filesButton).addComponent(
										addFileButton).addComponent(removeButton)).addPreferredGap(
								javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jLabel2).addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.PREFERRED_SIZE, 197,
										javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
	}// </editor-fold>//GEN-END:initComponents

	private void projectsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_projectsButtonActionPerformed
		importDialogProvider.setOpenedProjectsList();
		addFileButton.setEnabled(false);
	}// GEN-LAST:event_projectsButtonActionPerformed

	private void filesButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_filesButtonActionPerformed
		importDialogProvider.setOpenedProjectsList();
		addFileButton.setEnabled(true);
	}// GEN-LAST:event_filesButtonActionPerformed

	private void addFileButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_addFileButtonActionPerformed
		importDialogProvider.addFileToImportFileList();
		importDialogProvider.setOpenedProjectsList();
	}// GEN-LAST:event_addFileButtonActionPerformed

	private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_removeButtonActionPerformed
		if (projectsButton.isSelected()) {
			importDialogProvider.removeProjectsFromImportProjectsList(projectsList
					.getSelectedIndices());
		} else {
			importDialogProvider.removeFilesFromImportFilesList(projectsList
					.getSelectedIndices());
		}

		importDialogProvider.setOpenedProjectsList();
	}// GEN-LAST:event_removeButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JButton addFileButton;
	private javax.swing.ButtonGroup buttonGroup;
	protected javax.swing.JRadioButton filesButton;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JScrollPane jScrollPane1;
	protected javax.swing.JRadioButton projectsButton;
	protected javax.swing.JList projectsList;
	private javax.swing.JButton removeButton;
	// End of variables declaration//GEN-END:variables

}
