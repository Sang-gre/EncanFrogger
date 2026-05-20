[Setup]
AppName=EncanFrogger
AppVersion=1.0
DefaultDirName={autopf}\EncanFrogger
DefaultGroupName=EncanFrogger
OutputDir=..\..\Installer\Windows
OutputBaseFilename=EncanFroggerSetup
SetupIconFile=..\..\EncanFrogger\assets\icons\EncanFroggerLogo.ico
Compression=lzma
SolidCompression=yes

[Files]
Source: "..\Windows\EncanFrogger.exe"; DestDir: "{app}"
Source: "..\Windows\assets\*"; DestDir: "{app}\assets"; Flags: recursesubdirs

[Icons]
Name: "{group}\EncanFrogger"; Filename: "{app}\EncanFrogger.exe"; WorkingDir: "{app}"
Name: "{commondesktop}\EncanFrogger"; Filename: "{app}\EncanFrogger.exe"; WorkingDir: "{app}"

[Run]
Filename: "{app}\EncanFrogger.exe"; WorkingDir: "{app}"; Description: "Launch EncanFrogger"; Flags: nowait postinstall skipifsilent