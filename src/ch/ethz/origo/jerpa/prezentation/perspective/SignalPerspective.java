package ch.ethz.origo.jerpa.prezentation.perspective;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.swingx.JXDialog;
import org.jdesktop.swingx.JXTaskPane;

import ch.ethz.origo.jerpa.application.perspective.signalprocess.SignalSessionManager;
import ch.ethz.origo.jerpa.application.perspective.signalprocess.project.SignalPerspectiveObservable;
import ch.ethz.origo.jerpa.data.JERPAUtils;
import ch.ethz.origo.jerpa.data.perspective.signalprocess.SignalProjectWriter;
import ch.ethz.origo.jerpa.jerpalang.LangUtils;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.ArtefactSelectionDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.BaselineCorrectionDialog;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.SignalsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.averaging.AveragingPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.head.ChannelsPanelProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.importdialog.ImportDialogProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.info.SignalInfoProvider;
import ch.ethz.origo.jerpa.prezentation.perspective.signalprocess.output.ExportFrameProvider;
import ch.ethz.origo.juigle.application.exception.JUIGLELangException;
import ch.ethz.origo.juigle.application.exception.JUIGLEMenuException;
import ch.ethz.origo.juigle.application.exception.PerspectiveException;
import ch.ethz.origo.juigle.application.exception.ProjectOperationException;
import ch.ethz.origo.juigle.application.observers.IObservable;
import ch.ethz.origo.juigle.application.observers.IObserver;
import ch.ethz.origo.juigle.application.observers.JUIGLEObservable;
import ch.ethz.origo.juigle.application.observers.LanguageObservable;
import ch.ethz.origo.juigle.application.project.Project;
import ch.ethz.origo.juigle.data.JUIGLEErrorParser;
import ch.ethz.origo.juigle.prezentation.JUIGLEFileChooser;
import ch.ethz.origo.juigle.prezentation.JUIGLEGraphicsUtils;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLEMenuItem;
import ch.ethz.origo.juigle.prezentation.JUIGLEPerspectiveMenu;
import ch.ethz.origo.juigle.prezentation.JUIGLErrorInfoUtils;
import ch.ethz.origo.juigle.prezentation.dialogs.AboutDialog;
import ch.ethz.origo.juigle.prezentation.perspective.Perspective;

/**
 * 
 * 
 * @author Vaclav Souhrada (v.souhrada at gmail.com)
 * @version 0.3.6 (3/24/2010)
 * @since 0.1.0 (05/18/09)
 * @see Perspective
 * @see IObserver
 * 
 */
public class SignalPerspective extends Perspective implements IObserver {

	/** Only for serialization */
	private static final long serialVersionUID = 3313465073940475745L;

	// file menu items
	private JUIGLEMenuItem fileMenu;
	private JUIGLEMenuItem openFileItem;
	private JUIGLEMenuItem saveFileItem;
	private JUIGLEMenuItem saveAsFileItem;
	private JUIGLEMenuItem closeFileItem;
	private JUIGLEMenuItem importItem;
	private JUIGLEMenuItem exitItem;
	// edit menu items
	private JUIGLEMenuItem editMenu;
	private JUIGLEMenuItem undoItem;
	private JUIGLEMenuItem redoItem;
	private JUIGLEMenuItem baselineCorrItem;
	private JUIGLEMenuItem autoArteSelItem;
	// view menu items
	private JUIGLEMenuItem viewMenu;
	private JUIGLEMenuItem channelItem;
	private JUIGLEMenuItem editInfoWinItem;
	private JUIGLEMenuItem signalsWinItem;
	private JUIGLEMenuItem averagingWinItem;
	// help menu items
	private JUIGLEMenuItem helpItem;
	private JUIGLEMenuItem keyboardShortcutItem;
	private JUIGLEMenuItem aboutItem;
	// Grid bag constraints for mains panels
	private GridBagConstraints gbcSignalPanelProv;
	private GridBagConstraints gbcAveragingProv;
	private GridBagConstraints gbcSignalInfoProv;
	private GridBagConstraints gbcChannelProv;

	/** Session manager for this perspective */
	private SignalSessionManager sessionManager;

	private SignalPerspectiveObservable spObservable;

	private SignalInfoProvider signalInfoProvider;
	private AveragingPanelProvider averagingPanelProvider;
	private SignalsPanelProvider signalPanelProvider;
	private ChannelsPanelProvider channelPanelProvider;

	private ArtefactSelectionDialog artefactSelectionDialog;
	private BaselineCorrectionDialog baselineCorrectionDialog;

	private ExportFrameProvider efp;

	/**
	 * Default constructor. Initializes required objects.
	 */
	public SignalPerspective() {
		perspectiveObservable.attach(this);
		spObservable = SignalPerspectiveObservable.getInstance();
		spObservable.attach(this);
		sessionManager = new SignalSessionManager();
		efp = new ExportFrameProvider(sessionManager);
		resourcePath = LangUtils
				.getPerspectiveLangPathProp("perspective.signalprocessing.lang");
	}

	@Override
	public void initPerspectivePanel() throws PerspectiveException {
		super.initPerspectivePanel();
		mainPanel.setLayout(new GridBagLayout());
		try {
			signalInfoProvider = new SignalInfoProvider(sessionManager);
			averagingPanelProvider = new AveragingPanelProvider(sessionManager,
					SignalPerspectiveObservable.getInstance());
			signalPanelProvider = new SignalsPanelProvider(sessionManager);
			channelPanelProvider = new ChannelsPanelProvider(sessionManager);

			gbcSignalInfoProv = new GridBagConstraints();
			gbcSignalInfoProv.gridx = 0;
			gbcSignalInfoProv.gridy = 1;
			gbcSignalInfoProv.anchor = GridBagConstraints.CENTER;
			gbcSignalInfoProv.fill = GridBagConstraints.BOTH;
			gbcSignalInfoProv.weightx = 0.4;
			gbcSignalInfoProv.weighty = 0.5;

			gbcChannelProv = new GridBagConstraints();
			gbcChannelProv.gridx = 0;
			gbcChannelProv.gridy = 0;
			gbcChannelProv.anchor = GridBagConstraints.CENTER;
			gbcChannelProv.fill = GridBagConstraints.BOTH;
			gbcChannelProv.weightx = 0.4;
			gbcChannelProv.weighty = 0.5;

			gbcSignalPanelProv = new GridBagConstraints();
			gbcSignalPanelProv.gridx = 1;
			gbcSignalPanelProv.gridy = 0;
			gbcSignalPanelProv.anchor = GridBagConstraints.CENTER;
			gbcSignalPanelProv.fill = GridBagConstraints.BOTH;
			gbcSignalPanelProv.weightx = 0.6;
			gbcSignalPanelProv.weighty = 0.5;
			gbcSignalPanelProv.insets = new Insets(0, 0, 0, 0);

			gbcAveragingProv = new GridBagConstraints();
			gbcAveragingProv.gridx = 1;
			gbcAveragingProv.gridy = 1;
			gbcAveragingProv.anchor = GridBagConstraints.CENTER;
			gbcAveragingProv.fill = GridBagConstraints.BOTH;
			gbcAveragingProv.weightx = 0.6;
			gbcAveragingProv.weighty = 0.5;

			mainPanel.add(signalPanelProvider.getPanel(), gbcSignalPanelProv);
			mainPanel.add(averagingPanelProvider.getPanel(), gbcAveragingProv);
			mainPanel.add(channelPanelProvider.getPanel(), gbcChannelProv);
			mainPanel.add(signalInfoProvider.getPanel(), gbcSignalInfoProv);
		} catch (JUIGLELangException e) {
			throw new PerspectiveException(e);
		}
	}

	@Override
	public void initPerspectiveMenuPanel() throws PerspectiveException {
		if (menuTaskPane == null) {
			// menuTitledPanel = new JXTitledPanel();
			// menuTitledPanel.setOpaque(false);
			menuTaskPane = new JXTaskPane();
			menuTaskPane.setOpaque(false);

			// initalize menu
			menu = new JUIGLEPerspectiveMenu(JUIGLEMenu.MENU_LOCATION_TOP,
					resourcePath);
			menu.setFloatable(false);
			menu.setRollover(true);
			// initialize and add menu items
			initAndAddMenuItems();
		}
	}

	/**
	 * 
	 * @throws PerspectiveException
	 * @since 0.1.0
	 */
	private void initAndAddMenuItems() throws PerspectiveException {
		try {
			// add items to menu
			menu.addItem(initAndGetFileMenuItem());
			menu.addItem(initAndGetEditMenuItem());
			menu.addItem(initAndGetViewMenuItem());
			menu.addItem(initAndGetHelpMenuItem());
			menu.addMenuSeparator();
			menu.addHeaderHideButton(true);
			menu.addFooterHideButton(true);
			// menu.addMenuSeparator();
			initAndAddToolbarItems();
			menu.addMenuSeparator();
			// menuTitledPanel.add(menu);
			menuTaskPane.add(menu);
		} catch (JUIGLEMenuException e1) {
			throw new PerspectiveException(e1);
		}
	}

	public Icon getIcon() {
		return JUIGLEGraphicsUtils.createImageIcon(JERPAUtils.IMAGE_PATH
				+ "icon.gif", "aaaaaaaaaaaaaa");
	}

	@Override
	public void updateText() {
		super.updateText();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// update menu text
				menu.updateText();
			}
		});
	}

	/**
	 * 
	 * @return
	 * @version 0.1.1
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetFileMenuItem() {
		fileMenu = new JUIGLEMenuItem(getLocalizedString("menu.file"));
		// initialize subItems of file menu
		openFileItem = new JUIGLEMenuItem();
		saveFileItem = new JUIGLEMenuItem();
		saveAsFileItem = new JUIGLEMenuItem();
		closeFileItem = new JUIGLEMenuItem();
		importItem = new JUIGLEMenuItem();
		exitItem = new JUIGLEMenuItem();
		// set Resource bundles
		fileMenu.setResourceBundleKey("menu.file");
		openFileItem.setResourceBundleKey("menu.open");
		saveFileItem.setResourceBundleKey("menu.save");
		saveAsFileItem.setResourceBundleKey("menu.saveAs");
		closeFileItem.setResourceBundleKey("menu.close.project");
		importItem.setResourceBundleKey("menu.import");
		exitItem.setResourceBundleKey("menu.exit");
		// set actions to menu items
		setFileMenuActions();
		// add key accelerators to items
		openFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
				KeyEvent.CTRL_MASK));
		saveFileItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				KeyEvent.CTRL_MASK));
		// add subitems to file menu
		fileMenu.addSubItem(openFileItem);
		fileMenu.addSubItem(saveFileItem);
		fileMenu.addSubItem(saveAsFileItem);
		fileMenu.addSubItem(closeFileItem);
		fileMenu.addSubItem(importItem);
		fileMenu.addSubItem(exitItem);

		return fileMenu;
	}

	/**
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetEditMenuItem() {
		editMenu = new JUIGLEMenuItem(getLocalizedString("menu.edit"));
		undoItem = new JUIGLEMenuItem();
		redoItem = new JUIGLEMenuItem();
		baselineCorrItem = new JUIGLEMenuItem();
		autoArteSelItem = new JUIGLEMenuItem();
		//
		editMenu.setResourceBundleKey("menu.edit");
		undoItem.setResourceBundleKey("menu.edit.undo");
		redoItem.setResourceBundleKey("menu.edit.redo");
		baselineCorrItem.setResourceBundleKey("menu.edit.baselinecorrection");
		autoArteSelItem.setResourceBundleKey("menu.edit.autoselarte");
		//
		setEditMenuActions();
		//
		editMenu.addSubItem(undoItem);
		editMenu.addSubItem(redoItem);
		editMenu.addSubItem(baselineCorrItem);
		editMenu.addSubItem(autoArteSelItem);

		return editMenu;
	}

	/**
	 * 
	 * @return
	 * 
	 * @version 0.1.1 (3/20/2010)
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetViewMenuItem() {
		viewMenu = new JUIGLEMenuItem(getLocalizedString("menu.view"));
		channelItem = new JUIGLEMenuItem();
		signalsWinItem = new JUIGLEMenuItem();
		editInfoWinItem = new JUIGLEMenuItem();
		averagingWinItem = new JUIGLEMenuItem();
		//
		viewMenu.setResourceBundleKey("menu.view");
		channelItem.setResourceBundleKey("menu.view.channelwin");
		signalsWinItem.setResourceBundleKey("menu.view.signalwin");
		editInfoWinItem.setResourceBundleKey("menu.view.infowin");
		averagingWinItem.setResourceBundleKey("menu.view.averagewin");
		// set actions
		signalsWinItem.setAction(getSignalViewAction());
		averagingWinItem.setAction(getAveragePanelViewAction());
		editInfoWinItem.setAction(getSignalInfoPanelViewAction());
		channelItem.setAction(getChannelPanelViewAction());
		//
		viewMenu.addSubItem(channelItem);
		viewMenu.addSubItem(editInfoWinItem);
		viewMenu.addSubItem(signalsWinItem);
		viewMenu.addSubItem(averagingWinItem);

		return viewMenu;
	}

	/**
	 * 
	 * 
	 * @return
	 * @since 0.1.0
	 */
	private JUIGLEMenuItem initAndGetHelpMenuItem() {
		helpItem = new JUIGLEMenuItem(getLocalizedString("menu.help"));
		keyboardShortcutItem = new JUIGLEMenuItem();
		aboutItem = new JUIGLEMenuItem();
		//
		keyboardShortcutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K,
				KeyEvent.CTRL_MASK));
		//
		helpItem.setResourceBundleKey("menu.help");
		keyboardShortcutItem.setResourceBundleKey("menu.help.keyboard.shortcuts");
		aboutItem.setResourceBundleKey("menu.help.about.signalprocessing");
		//
		setHelpMenuActions();
		helpItem.addSubItem(keyboardShortcutItem);
		helpItem.addSubItem(aboutItem);
		return helpItem;
	}

	/**
	 * 
	 * 
	 * @version 0.1.3 (3/24/2010)
	 * @since 0.1.1
	 */
	private void setFileMenuActions() {
		Action importAct = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = 5259515978643788611L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					new ImportDialogProvider(sessionManager);
				} catch (PerspectiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		// init exit app action
		Action exitAct = new AbstractAction() {
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JUIGLEObservable.getInstance().setState(
						JUIGLEObservable.MSG_APPLICATION_CLOSING);
			}
		};
		openFileItem.setAction(getOpenFileAction());
		saveFileItem.setAction(getSaveAction());
		saveAsFileItem.setAction(getSaveAsAction());
		importItem.setAction(importAct);
		exitItem.setAction(exitAct);
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @version 0.1.0 (3/21/2010)
	 * @since 0.3.5 (3/21/2010)
	 */
	private Action getSaveAction() {
		Action saveAction = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					sessionManager.saveFile(new SignalProjectWriter());
				} catch (ProjectOperationException e1) {
					JUIGLErrorInfoUtils.showErrorDialog("Project Error",
							JUIGLEErrorParser.getJUIGLEErrorMessage(e1.getMessage()), e1,
							java.util.logging.Level.WARNING);
				}
			}
		};
		return saveAction;
	}

	/**
	 * 
	 * 
	 * @return
	 * 
	 * @version 0.1.0 (3/21/2010)
	 * @since 0.3.5 (3/21/2010)
	 */
	private Action getSaveAsAction() {
		Action saveAsAct = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = 5259515978643788611L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					sessionManager.saveAsFile();
				} catch (ProjectOperationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		return saveAsAct;
	}

	/**
	 * @since 0.1.0
	 */
	private void setEditMenuActions() {
		Action autoArtefactSelAct = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				artefactSelectionDialog.setActualLocationAndVisibility();
			}
		};
		Action baselineDialogAct = new AbstractAction() {
			/** Only for serialization */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				baselineCorrectionDialog.setActualLocationAndVisibility();
			}
		};
		autoArteSelItem.setAction(autoArtefactSelAct);
		baselineCorrItem.setAction(baselineDialogAct);
	}

	/**
	 * @since 0.1.0
	 */
	private void setHelpMenuActions() {
		Action about = new AbstractAction() {
			/**  */
			private static final long serialVersionUID = -1644285485867277600L;

			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new AboutDialog();
					}
				});
			}
		};
		aboutItem.setAction(about);
	}

	@Override
	public void update() {
		// not implemented
	}

	@Override
	public void update(Object state) {
		makeUpdate(state);
	}

	/**
	 * 
	 * @param obj
	 * @version 0.1.0
	 * @since 0.2.0
	 */
	private void makeUpdate(Object obj) {
	}

	/**
	 * 
	 * @version 0.1.0 (3/21/2010)
	 * @since 0.3.5 (3/21/2010)
	 */
	private void initAndAddToolbarItems() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				JUIGLEMenuItem openTBItem = new JUIGLEMenuItem();
				JUIGLEMenuItem saveTBItem = new JUIGLEMenuItem();
				// set actions
				openTBItem.setAction(getOpenFileAction());
				saveTBItem.setAction(getSaveAction());
				// set tooltip
				openTBItem.setToolTipText(getLocalizedString("toolbar.open.tooltip"));
				openTBItem.setToolTipResourceBundleKey("toolbar.open.tooltip");
				saveTBItem.setToolTipText(getLocalizedString("toolbar.save.tooltip"));
				saveTBItem.setToolTipResourceBundleKey("toolbar.save.tooltip");
				// set icons
				try {
					openTBItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "folder_48.png", 32, 32));
					saveTBItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "save_48.png", 32, 32));
				} catch (PerspectiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				menu.addItem(openTBItem);
				menu.addItem(saveTBItem);
				menu.revalidate();
				menu.repaint();
			}
		});
	}

	/**
	 * 
	 * 
	 * @throws JUIGLEMenuException
	 * @version 0.1.1 (3/20/2010)
	 * @since 0.2.1 (2/14/2010)
	 */
	private void addOpenedFunctionsToMenu() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					JUIGLEMenuItem signalPanelItem = new JUIGLEMenuItem();
					JUIGLEMenuItem infoPanelItem = new JUIGLEMenuItem();
					JUIGLEMenuItem channelPanelItem = new JUIGLEMenuItem();
					JUIGLEMenuItem averagePanelItem = new JUIGLEMenuItem();
					// set actions
					signalPanelItem.setAction(getSignalViewAction());
					averagePanelItem.setAction(getAveragePanelViewAction());
					infoPanelItem.setAction(getSignalInfoPanelViewAction());
					// tooltip
					signalPanelItem
							.setToolTipText(getLocalizedString("toolbar.view.signalwin"));
					signalPanelItem.setToolTipResourceBundleKey("toolbar.view.signalwin");
					infoPanelItem
							.setToolTipText(getLocalizedString("toolbar.view.infowin"));
					infoPanelItem.setToolTipResourceBundleKey("toolbar.view.infowin");
					averagePanelItem
							.setToolTipText(getLocalizedString("toolbar.view.averagewin"));
					averagePanelItem
							.setToolTipResourceBundleKey("toolbar.view.averagewin");
					// init images
					signalPanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "icon.gif", 24, 24));
					infoPanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "info-48.png", 24, 24));
					averagePanelItem.setIcon(JUIGLEGraphicsUtils.createImageIcon(
							JERPAUtils.IMAGE_PATH + "averages32.png", 24, 24));
					menu.addMenuSeparator();
					menu.addItem(signalPanelItem);
					menu.addItem(infoPanelItem);
					menu.addItem(averagePanelItem);
				} catch (PerspectiveException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JUIGLEMenuException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				menu.revalidate();
				menu.repaint();
			}
		});
	}

	@Override
	public void update(IObservable o, Object state) {
		makeUpdate(o, state);
	}

	private Action getSignalViewAction() {
		Action signPanelShow = new AbstractAction() {
			private static final long serialVersionUID = 5222085948094114535L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openComponentInDialog(signalPanelProvider.getPanel(),
						gbcSignalPanelProv);
			}
		};
		return signPanelShow;
	}

	private Action getAveragePanelViewAction() {
		Action averagePanelShow = new AbstractAction() {
			private static final long serialVersionUID = 5222085948094114535L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openComponentInDialog(averagingPanelProvider.getPanel(),
						gbcAveragingProv);
			}
		};
		return averagePanelShow;
	}

	private Action getChannelPanelViewAction() {
		Action channelPanelShow = new AbstractAction() {
			private static final long serialVersionUID = 5222085948094114535L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openComponentInDialog(channelPanelProvider.getPanel(), gbcChannelProv);
			}
		};
		return channelPanelShow;
	}

	private Action getSignalInfoPanelViewAction() {
		Action signalInfoPanelShow = new AbstractAction() {
			private static final long serialVersionUID = 5222085948094114535L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				openComponentInDialog(signalInfoProvider.getPanel(), gbcSignalInfoProv);
			}
		};
		return signalInfoPanelShow;
	}

	private Action getOpenFileAction() {
		Action open = new AbstractAction() {
			private static final long serialVersionUID = -6603743681967057946L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JUIGLEFileChooser fileChooser = new JUIGLEFileChooser();
				fileChooser.setDialogTitle("Open file");
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"jERPA project file (*.erpa)", "erpa"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"European Data Format (*.edf, *.rec)", "edf", "rec"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"Pseudo signal generator (*.generator)", "generator"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"BrainStudio Format (*.xml)", "xml"));
				fileChooser.setFileFilter(new FileNameExtensionFilter(
						"Brain Vision Data Exchange Header File (*.vhdr)", "vhdr"));
				fileChooser
						.setFileFilter(new FileNameExtensionFilter(
								"All supported files (*.edf, *.erpa, *.generator, *.rec, *.vhdr, *.xml)",
								"edf", "esp", "generator", "rec", "vhdr", "xml"));

				fileChooser.setAcceptAllFileFilterUsed(false);

				if (fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					try {
						sessionManager.loadFile(file);
						addOpenedFunctionsToMenu();
						spObservable
								.setState(SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED);

					} catch (ProjectOperationException e1) {
						// FIXME upravit na vypis do GUI JERPA011
						e1.printStackTrace();
					}
				}
			}
		};
		return open;
	}

	public void createNewArtefactDialog() {
		try {
			artefactSelectionDialog = new ArtefactSelectionDialog(sessionManager);
			baselineCorrectionDialog = new BaselineCorrectionDialog(sessionManager);
		} catch (JUIGLELangException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Open component in a new <code>JXDialog</code>.
	 * 
	 * @param component
	 *          which will be added to the dialog
	 * @param gbcPosition
	 *          {@link GridBagConstraints} GridBagConstraints position
	 * 
	 * @version 0.1.0 (3/20/2010)
	 * @since 0.3.4 (3/20/2010)
	 */
	private void openComponentInDialog(final JComponent component,
			final GridBagConstraints gbcPosition) {
		JXDialog dialog = new JXDialog(component);
		dialog.setSize(800, 600);
		dialog.setAlwaysOnTop(true);
		// dialog.pack();
		dialog.setLocation(JUIGLEGraphicsUtils.getCenterPosition(dialog));
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				addSignalPanelToContentPane(component, gbcPosition);
			}

			public void windowClosed(WindowEvent e) {
				addSignalPanelToContentPane(component, gbcPosition);
			}

			private void addSignalPanelToContentPane(Component component,
					GridBagConstraints gbcPosition) {
				mainPanel.add(component, gbcPosition);
				mainPanel.revalidate();
				mainPanel.repaint();
			}
		});
	}

	private void checkUndoableControls() {
		Project project = sessionManager.getCurrentProject();
		// undoButton.setEnabled(project.canUndo());
		undoItem.setEnabled(project.canUndo());
		// mainWindow.redoButton.setEnabled(project.canRedo());
		redoItem.setEnabled(project.canRedo());
	}

	/**
	 * 
	 * @param object
	 * @param state
	 * @version 0.1.0
	 * @since 0.2.0
	 */
	private void makeUpdate(IObservable o, Object state) {
		int currState;

		if (state instanceof java.lang.Integer) {
			currState = ((Integer) state).intValue();
		} else {
			return;
		}

		if (o instanceof SignalPerspectiveObservable) {
			switch (currState) {
			case SignalPerspectiveObservable.MSG_PROJECT_CLOSED:
				saveFileItem.setEnabled(false);
				saveAsFileItem.setEnabled(false);
				closeFileItem.setEnabled(false);
				// FIXME mainWindow.saveButton.setEnabled(false);
				autoArteSelItem.setEnabled(false);
				baselineCorrItem.setEnabled(false);
				// FIXME mainWindow.invertSignalsButton.setEnabled(false);
				break;

			case SignalPerspectiveObservable.MSG_CURRENT_PROJECT_CHANGED:
				checkUndoableControls();
				// FIXME addOpenedProjectsToMenu(sessionManager.getProjectsNames(), 0);
				sessionManager.getAutoSelectionArtefact().setCurrentData();
				sessionManager.getBaselineCorrection().setCurrentData();
				saveAsFileItem.setEnabled(true);
				// FIXME mainWindow.saveButton.setEnabled(true);
				saveFileItem.setEnabled(true);
				closeFileItem.setEnabled(true);
				autoArteSelItem.setEnabled(true);
				baselineCorrItem.setEnabled(true);
				createNewArtefactDialog();
				// FIXME mainWindow.invertSignalsButton.setEnabled(true);
				// FIXME
				// mainWindow.invertSignalsButton.setSelected(sessionManager.getCurrentProject()
				// .isInvertedSignalsView());
				break;

			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_RESUME:
			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_START:
				// FIXME mainWindow.openButton.setEnabled(false);
				openFileItem.setEnabled(false);
				autoArteSelItem.setEnabled(false);
				baselineCorrItem.setEnabled(false);
				// FIXME mainWindow.undoButton.setEnabled(false);
				undoItem.setEnabled(false);
				// FIXME mainWindow.redoButton.setEnabled(false);
				redoItem.setEnabled(false);
				// FIXME mainWindow.invertSignalsButton.setEnabled(false);
				break;

			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_PAUSE:
			case SignalPerspectiveObservable.MSG_SIGNAL_PLAYBACK_STOP:
				// FIXME mainWindow.openButton.setEnabled(true);
				openFileItem.setEnabled(true);
				autoArteSelItem.setEnabled(true);
				baselineCorrItem.setEnabled(true);
				// FIXME mainWindow.undoButton.setEnabled(true);
				undoItem.setEnabled(true);
				// FIXME mainWindow.redoButton.setEnabled(true);
				redoItem.setEnabled(true);
				// FIXME mainWindow.invertSignalsButton.setEnabled(true);
				break;

			case SignalPerspectiveObservable.MSG_UNDOABLE_COMMAND_INVOKED:
				checkUndoableControls();
				break;

			case SignalPerspectiveObservable.MSG_NEW_BUFFER:
				sessionManager.getAutoSelectionArtefact().setCurrentData();
				sessionManager.getBaselineCorrection().setCurrentData();
				break;

			case SignalPerspectiveObservable.MSG_BASELINE_CORRECTION_INTERVAL_SELECTED:
				baselineCorrectionDialog.setSpinnersValues(sessionManager
						.getBaselineCorrection().getStartTimeStamp(), sessionManager
						.getBaselineCorrection().getEndTimeStamp());
				baselineCorrectionDialog.setActualLocationAndVisibility();
				break;

			case SignalPerspectiveObservable.MSG_SHOW_IMPORT_DIALOG:
				// mainWindow.setEnabled(false);
				// FIXME not used yet
				break;

			case SignalPerspectiveObservable.MSG_MODAL_DIALOG_CLOSED:
				// mainWindow.setEnabled(true);
				// FIXME not used yet
				break;

			case SignalPerspectiveObservable.MSG_INVERTED_SIGNALS_VIEW_CHANGED:
				// FIXME
				// mainWindow.invertSignalsButton.setSelected(sessionManager.getCurrentProject()
				// .isInvertedSignalsView());
				break;
			case SignalPerspectiveObservable.MSG_SEND_CURRENT_PROJECT:
				spObservable.sendObjectToObservers(sessionManager.getCurrentProject());
				break;
			}
		} else if (o instanceof LanguageObservable) {
			currState = (Integer) state;
			if (currState == LanguageObservable.MSG_LANGUAGE_CHANGED) {
				updateText();
			}
		}
	}

}