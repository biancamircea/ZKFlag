import React from 'react';
import {Avatar, Dialog, DialogTitle, List, ListItem, ListItemAvatar, ListItemButton, ListItemText} from "@mui/material";
import {blue} from "@mui/material/colors";
import LabelIcon from '@mui/icons-material/Label';
function TagDialog({ onClose, open, tags }) {
    const handleClose = () => {
        onClose(null);
    };
    const handleListItemClick = (value) => {
        onClose(value);
    };

    const tagsEl = tags.map(tag => {
        return (
            <ListItem
                disableGutters
                key={tag.id}
            >
                <ListItemButton
                    onClick={() => handleListItemClick(tag.id)}
                    key={tag.id}
                    sx={{
                        padding: '1em 5em'
                    }}
                >
                    <ListItemAvatar>
                        <Avatar sx={{ bgcolor: blue[100], color: blue[600] }}>
                            <LabelIcon />
                        </Avatar>
                    </ListItemAvatar>
                    <ListItemText primary={tag.labelName} />
                </ListItemButton>
            </ListItem>
        )
    })

    return (
        <Dialog onClose={handleClose} open={open}>
            <DialogTitle>Add tag to feature toggle</DialogTitle>
            <List sx={{
                width: '100%',
                maxWidth: 360,
                bgcolor: 'background.paper',
                position: 'relative',
                overflow: 'auto',
                maxHeight: 300,
                '& ul': { padding: 0 },
            }}>
                {
                    tagsEl.length === 0 ?
                        (<h4 style={{padding: '0 3em'}}>No tag available</h4>) :
                        tagsEl
                }
            </List>
        </Dialog>
    );
}

export default TagDialog;